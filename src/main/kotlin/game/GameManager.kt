package game

class GameManager {

    private val gamesByName: MutableMap<String, GameLogic> = HashMap()
    private val gamesByUserId: MutableMap<String, GameLogic> = HashMap()

    fun createGame(userId: String, gameName: String, cardpackIds: List<String>): GameLogic.FOVGameInfo {
        TODO()
    }

    fun joinGame(userId: String, gameName: String) {
        TODO()
    }

    fun leaveGame(userId: String) {
        TODO()
    }


    fun vote(userId: String, cardId: String) {
        TODO()
    }

    fun play(userId: String, cardId: String) {
        TODO()
    }


    fun getUserFOV(userId: String): GameLogic.FOVGameInfo? {
        TODO()
    }

    fun getInfoList(): List<GameLogic.Info> {
        TODO()
    }

}