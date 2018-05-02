package game

import api.*
import model.FOVGameData
import model.GameInfo

class GameManager(private val cardFetcher: CardFetcher) {

    private val gamesByName: MutableMap<String, Game> = HashMap()
    private val gamesByUserId: MutableMap<String, Game> = HashMap()

    fun createGame(userId: String, gameName: String, maxPlayers: Int, cardpackIds: List<String>): FOVGameData {
        val cards = cardFetcher.getCards(cardpackIds)
        val game = Game(gameName, maxPlayers, cards.first, cards.second)
        game.join(userId)
        val gameData = game.getFOV(userId)
        gamesByName[gameName] = game
        gamesByUserId[userId] = game
        return gameData
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
        if (game.isEmpty()) {
            gamesByName.remove(game.name)
        }
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
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        return game.getFOV(userId)
    }

    fun getInfoList(): List<GameInfo> {
        return gamesByName.toList().sortedWith(compareBy({it.second.name})).map { game -> game.second.getInfo() }
    }

}