package game

import java.util.*

class Game(internal var maxPlayers: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard> /* TODO - Add socket handler as arg */) {
    private var players: MutableList<Player> = ArrayList()
    private var ownerId: String? = null
    private var judgeId: String? = null
    private var stage: GameStage = GameStage.NOT_RUNNING
    private var nextStage: Date? = null
    private var timer: Timer? = null
    private var whitePlayed: Map<Int, Array<WhiteCard>>? = null
    private var whiteDeck = WhiteCardDeck(whiteCards)
    private var blackDeck = BlackCardDeck(blackCards)

    private val isRunning: Boolean
        get() = stage != GameStage.NOT_RUNNING


    fun start(userId: String) {
        if (userId != judgeId) {
            throw InsufficientAccessException("Must be owner to start game")
        } else if (isRunning) {
            throw Exception("Game is already running")
        } else if (players.size < 3) {
            throw Exception("Must have at least 3 players to start game")
        }
        TODO()
    }

    fun stop(userId: String) {
        if (userId != judgeId) {
            throw InsufficientAccessException("Must be owner to stop game")
        } else if (!isRunning) {
            throw Exception("Game is not running")
        }
        TODO()
    }


    fun join(userId: String) {
        players.add(Player(userId))
    }

    fun leave(userId: String) {}

    fun kickUser(kickerId: String, kickeeId: String) {
        if (kickerId != ownerId) {
            throw InsufficientAccessException("Must be game owner to kick user")
        } else if (kickerId == kickeeId) {
            throw InsufficientAccessException("Cannot kick yourself from the game")
        }
    }

    fun playCard(userId: String, cardId: String) {}

    fun voteCard(userId: String, cardId: String) {}

    fun getFOV(userId: String): FOVGameInfo {
        TODO()
    }

    enum class GameStage {
        NOT_RUNNING,
        PLAY_PLASE,
        JUDGE_PHASE,
        SCORE_PHASE
    }

    class FOVGameInfo

    class Info

    class Player(userId: String) {

        val id: String = userId

        var score = 0
            private set

        fun incrementScore() {
            score++
        }

    }

}