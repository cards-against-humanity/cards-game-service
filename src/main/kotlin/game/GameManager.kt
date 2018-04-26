package game

class GameManager {

    private val gamesByName: MutableMap<String, Game> = HashMap()
    private val gamesByUserId: MutableMap<String, Game> = HashMap()

    fun createGame(userId: String, gameName: String, cardpackIds: List<String>): Game.FOVGameInfo {
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


    fun getUserFOV(userId: String): Game.FOVGameInfo? {
        TODO()
    }

    fun getInfoList(): List<Game.Info> {
        TODO()
    }

}