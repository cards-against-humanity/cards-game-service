package game

import api.*
import model.FOVGameData
import model.GameInfo

class GameManager(private val cardFetcher: CardFetcher) {

    private val gamesByName: MutableMap<String, Game> = HashMap()
    private val gamesByUserId: MutableMap<String, Game> = HashMap()

    fun createGame(userId: String, gameName: String, cardpackIds: List<String>): FOVGameData {
        TODO()
    }

    fun joinGame(userId: String, gameName: String) {
        val game = gamesByName[gameName] ?: throw Exception("Game does not exist with name: $gameName")
        game.join(userId)
        gamesByUserId[userId] = game
    }

    fun leaveGame(userId: String) {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.leave(userId)
        gamesByUserId.remove(userId)
    }


    fun vote(userId: String, cardId: String) {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.voteCard(userId, cardId)
    }

    fun play(userId: String, cardId: String) {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.playCard(userId, cardId)
    }


    fun getUserFOV(userId: String): FOVGameData? {
        TODO()
    }

    fun getInfoList(): List<GameInfo> {
        TODO()
    }

}