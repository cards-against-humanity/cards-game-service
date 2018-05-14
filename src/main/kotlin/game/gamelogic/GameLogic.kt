package game.gamelogic

import game.*
import model.BlackCard
import model.WhiteCard

class GameLogic(private var maxPlayers: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard> /* TODO - Add socket handler as arg */) {
    private val handSize = 4
    private val minPlayersToStart = 3

    var stage: GameStage = GameStage.NOT_RUNNING
        private set

    private val _whitePlayed: MutableMap<String, MutableList<WhiteCard>> = HashMap()
    private val whiteDeck = WhiteCardDeck(whiteCards)
    private val blackDeck = BlackCardDeck(blackCards)
    private val playerManager: PlayerManager = PlayerManager(handSize, whiteDeck)

    private val isRunning: Boolean
        get() = stage != GameStage.NOT_RUNNING

    val ownerId: String?
        get() = if (playerManager.owner != null) { playerManager.owner!!.id } else { null }

    val judgeId: String?
        get() = if (playerManager.judge != null) { playerManager.judge!!.id } else { null }

    private val _players: Map<String, PlayerManager.MutablePlayer>
        get() = playerManager.players

    val players: Map<String, PlayerGameLogicModel>
        get() = playerManager.players

    val playersList: List<PlayerGameLogicModel>
        get() = playerManager.playersList

    val currentBlackCard: BlackCard?
        get() = if (isRunning) { blackDeck.currentCard } else { null }

    val whitePlayed: Map<String, List<WhiteCard>>
        get() = _whitePlayed

    init {
        val minCardCount = maxPlayers * (handSize + 4)
        if (maxPlayers < minPlayersToStart) {
            throw Exception("Max players must be at least $minPlayersToStart")
        }
        if (maxPlayers > 20) {
            throw Exception("Max players cannot be greater than 20")
        }
        if (whiteCards.size < minCardCount) {
            throw Exception("Not enough white cards, need ${minCardCount - whiteCards.size} more with this many players")
        }
    }

    fun start(userId: String) {
        when {
            userId != ownerId -> throw InsufficientAccessException("Must be owner to start game")
            isRunning -> throw Exception("Game is already running")
            players.size < minPlayersToStart -> throw Exception("Must have at least $minPlayersToStart players to start game")
            else -> {
                playerManager.nextJudge()
                stage = GameStage.PLAY_PHASE
            }
        }

    }

    fun stop(userId: String) {
        if (userId != ownerId) {
            throw InsufficientAccessException("Must be owner to stop game")
        }
        stop()
    }

    private fun stop() {
        if (!isRunning) {
            throw Exception("Game is not running")
        }

        _whitePlayed.forEach { cardList ->
            cardList.value.forEach { card -> whiteDeck.discardCard(card) }
            cardList.value.clear()
        }
        whiteDeck.reset()
        blackDeck.reset()
        playerManager.reset()

        stage = GameStage.NOT_RUNNING
    }

    fun join(userId: String) {
        if (players.size == maxPlayers) {
            throw Exception("Game is full")
        }
        playerManager.addUser(userId)
        _whitePlayed[userId] = ArrayList()
    }

    fun leave(userId: String) {
        if (userId == judgeId) {
            playerManager.resetPlayedCards()
            _whitePlayed.values.forEach { it.clear() }
            stage = GameStage.ROUND_END_PHASE
        }

        playerManager.removeUser(userId)
        _whitePlayed[userId]!!.forEach { card -> whiteDeck.discardCard(card) }
        _whitePlayed.remove(userId)
        if (isRunning && players.size < minPlayersToStart) {
            stop()
        }
    }

    fun kickUser(kickerId: String, kickeeId: String) {
        if (kickerId != ownerId) {
            throw InsufficientAccessException("Must be game owner to kick user")
        } else if (kickerId == kickeeId) {
            throw InsufficientAccessException("Cannot kick yourself from the game")
        }
        leave(kickeeId)
    }

    fun playCards(userId: String, cardIds: List<String>) {
        if (userId == judgeId) {
            throw Exception("Judge cannot play a card")
        } else if(stage != GameStage.PLAY_PHASE) {
            throw Exception("Cannot play cards right now")
        } else if (players[userId] == null) {
            throw Exception("User is not in the game")
        } else if (userHasPlayed(userId)) {
            throw Exception("You cannot play anymore cards for this round")
        } else if (cardIds.size != currentBlackCard!!.answerFields) {
            throw Exception("Must play exactly ${currentBlackCard!!.answerFields} cards")
        }

        // TODO - Write UT that tries using duplicate card ids

        val cards = cardIds.map { _players[userId]!!.playCard(it) }
        _whitePlayed[userId] = cards.toMutableList()

        if (allUsersHavePlayed()) {
            stage = GameStage.JUDGE_PHASE
        }
    }

    fun voteCard(userId: String, cardId: String) {
        if (userId != judgeId) {
            throw Exception("Must be the judge to vote for a card")
        } else if (stage != GameStage.JUDGE_PHASE) {
            throw Exception("Cannot vote for a card")
        } else if (!allUsersHavePlayed()) {
            throw Exception("Not all users have played")
        }

        val winningPlayerId = (whitePlayed.entries.find { it.value.map { it.id }.contains(cardId) } ?: throw Exception("No players have played the specified card")).key
        _players[winningPlayerId]!!.incrementScore()

        playerManager.drawAllPlayersToFull()
        _whitePlayed.values.forEach {
            it.forEach { whiteDeck.discardCard(it) }
            it.clear()
        }
        // TODO - Check if player has reached max score
    }

    fun startNextRound() {
        stage = GameStage.PLAY_PHASE
    }

    private fun userHasPlayed(userId: String): Boolean {
        return _whitePlayed[userId]!!.size == currentBlackCard!!.answerFields
    }

    private fun allUsersHavePlayed(): Boolean {
        players.values.map { it.id }.filter { it != judgeId }.forEach {
            if (!userHasPlayed(it)) {
                return false
            }
        }
        return true
    }

    enum class GameStage {
        NOT_RUNNING,
        PLAY_PHASE,
        JUDGE_PHASE,
        ROUND_END_PHASE
    }
}