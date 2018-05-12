package game.gamelogic

import game.*
import model.BlackCard
import model.WhiteCard

class GameLogic(private var maxPlayers: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard> /* TODO - Add socket handler as arg */) {
    private val handSize = 4
    private val minPlayersToStart = 3

    var stage: GameStage = GameStage.NOT_RUNNING
        private set

    private var _whitePlayed: MutableMap<String, MutableList<WhiteCard>> = HashMap()
    private var whiteDeck = WhiteCardDeck(whiteCards)
    private var blackDeck = BlackCardDeck(blackCards)
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

        _whitePlayed.forEach { cardList -> cardList.value.forEach { card -> whiteDeck.discardCard(card) } }
        _whitePlayed = HashMap()
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
        // TODO - Check if user was judge and react accordingly
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

    fun playCard(userId: String, cardId: String) {
        if (userId == judgeId) {
            throw Exception("Judge cannot play a card")
        } else if(stage != GameStage.PLAY_PHASE) {
            throw Exception("Cannot play cards right now")
        } else if (players[userId] == null) {
            throw Exception("User is not in the game")
        } else if (_whitePlayed[userId]!!.size == blackDeck.currentCard.answerFields) {
            throw Exception("You cannot play anymore cards for this round")
        }

        _whitePlayed[userId]!!.add(_players[userId]!!.playCard(cardId))

        if (!players.all { player -> _whitePlayed[player.value.id]!!.size == blackDeck.currentCard.answerFields }) {
            stage = GameStage.JUDGE_PHASE
        }
    }

    fun voteCard(userId: String, cardId: String) {
        if (userId != judgeId) {
            throw Exception("Must be the judge to vote for a card")
        } else if (stage != GameStage.JUDGE_PHASE) {
            throw Exception("Cannot vote for a card")
        }

        // TODO - Set winner
    }

    enum class GameStage {
        NOT_RUNNING,
        PLAY_PHASE,
        JUDGE_PHASE,
        SCORE_PHASE
    }
}