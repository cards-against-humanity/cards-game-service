package game.gamelogic

import game.*
import model.BlackCard
import model.WhiteCard

class GameLogic(private var maxPlayers: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard> /* TODO - Add socket handler as arg */) {
    private val handSize = 4

    var stage: GameStage = GameStage.NOT_RUNNING
        private set

    private var whitePlayed: MutableMap<String, MutableList<WhiteCard>> = HashMap()
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

    init {
        val minCardCount = maxPlayers * (handSize + 4)
        if (maxPlayers < 3) {
            throw Exception("Max players must be at least 3")
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
            players.size < 3 -> throw Exception("Must have at least 3 players to start game")
            else -> {
                playerManager.nextJudge()
                stage = GameStage.PLAY_PLASE
            }
        }

    }

    fun stop(userId: String) {
        if (userId != ownerId) {
            throw InsufficientAccessException("Must be owner to stop game")
        } else if (!isRunning) {
            throw Exception("Game is not running")
        }

        whitePlayed.forEach { cardList -> cardList.value.forEach { card -> whiteDeck.discardCard(card) } }
        whitePlayed = HashMap()
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
        whitePlayed[userId] = ArrayList()
    }

    fun leave(userId: String) {
        // TODO - Check if user was judge and react accordingly
        playerManager.removeUser(userId)
        whitePlayed[userId]!!.forEach { card -> whiteDeck.discardCard(card) }
        whitePlayed.remove(userId)
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
        } else if(stage != GameStage.PLAY_PLASE) {
            throw Exception("Cannot play cards right now")
        } else if (players[userId] == null) {
            throw Exception("User is not in the game")
        } else if (whitePlayed[userId]!!.size == blackDeck.currentCard.answerFields) {
            throw Exception("You cannot play anymore cards for this round")
        }

        whitePlayed[userId]!!.add(_players[userId]!!.playCard(cardId))

        if (!players.all { player -> whitePlayed[player.value.id]!!.size == blackDeck.currentCard.answerFields }) {
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
        PLAY_PLASE,
        JUDGE_PHASE,
        SCORE_PHASE
    }
}