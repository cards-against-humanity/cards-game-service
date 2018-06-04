package game.gamelogic

import com.fasterxml.jackson.annotation.JsonProperty
import game.*
import model.BlackCard
import model.WhiteCard

class GameLogic(private val maxPlayers: Int, private val maxScore: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard> /* TODO - Add socket handler as arg */) {
    private val handSize = 4
    private val minPlayersToStart = 3

    var stage: GameStage = GameStage.NOT_RUNNING
        private set

    var winnerId: String? = null
        private set

    private val whiteDeck = WhiteCardDeck(whiteCards, handSize)
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
        get() = whiteDeck.whitePlayed

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
        if (maxScore < 1) {
            throw Exception("Max score must be a positive number")
        }
    }

    fun start(userId: String) {
        winnerId = null
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

        whiteDeck.resetAndDrawNewHands()
        blackDeck.reset()
        playerManager.reset()

        stage = GameStage.NOT_RUNNING
    }

    fun join(userId: String) {
        if (players.size == maxPlayers) {
            throw Exception("Game is full")
        }
        whiteDeck.addUser(userId)
        playerManager.addUser(userId)
    }

    fun leave(userId: String) {
        if (userId == judgeId) {
            whiteDeck.revertPlayedCards()
            stage = GameStage.ROUND_END_PHASE
        }

        playerManager.removeUser(userId)
        whiteDeck.removeUser(userId)
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
        } else if (cardIds.size != currentBlackCard!!.answerFields) {
            throw Exception("Must play exactly ${currentBlackCard!!.answerFields} cards")
        }

        whiteDeck.playCards(userId, cardIds)

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

        winnerId = winningPlayerId
        if (_players[winningPlayerId]!!.score == maxScore) {
            stop()
        } else {
            stage = GameStage.ROUND_END_PHASE
        }
    }

    fun startNextRound() {
        if (stage != GameStage.ROUND_END_PHASE) {
            throw Exception("Cannot start the next round at this time")
        }
        winnerId = null
        playerManager.nextJudge()
        whiteDeck.discardPlayedCardsAndRedraw()
        blackDeck.setNewCard()
        stage = GameStage.PLAY_PHASE
    }

    private fun userHasPlayed(userId: String): Boolean {
        return whitePlayed[userId]!!.size == currentBlackCard!!.answerFields
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
        @JsonProperty("notRunning") NOT_RUNNING,
        @JsonProperty("playPhase") PLAY_PHASE,
        @JsonProperty("judgePhase") JUDGE_PHASE,
        @JsonProperty("roundEndPhase") ROUND_END_PHASE
    }
}