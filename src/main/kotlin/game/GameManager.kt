package game

import api.*
import model.FOVGameData
import model.GameInfo

class GameManager(private val userFetcher: UserFetcher, private val cardFetcher: CardFetcher) {

    private val gamesByName: MutableMap<String, Game> = HashMap()
    private val gamesByUserId: MutableMap<String, Game> = HashMap()

    fun createGame(userId: String, gameName: String, maxPlayers: Int, cardpackIds: List<String>): FOVGameData {
        val cards = cardFetcher.getCards(cardpackIds)
        val game = Game(gameName, maxPlayers, cards.first, cards.second, userFetcher)
        gamesByName[gameName] = game
        joinGame(userId, gameName)
        return getUserFOV(userId)!!
    }

    fun startGame(userId: String): FOVGameData {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.start(userId)
        return game.getFOV(userId)
    }

    fun stopGame(userId: String): FOVGameData {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.stop(userId)
        return game.getFOV(userId)
    }

    fun joinGame(userId: String, gameName: String): FOVGameData {
        val game = gamesByName[gameName] ?: throw Exception("Game does not exist with name: $gameName")

        if (gamesByUserId[userId] != null) {
            leaveGame(userId)
        }

        game.join(userId)
        gamesByUserId[userId] = game
        return game.getFOV(userId)
    }

    fun leaveGame(userId: String) {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.leave(userId)
        gamesByUserId.remove(userId)
        if (game.isEmpty()) {
            gamesByName.remove(game.name)
        }
    }

    fun kick(kickerId: String, kickeeId: String): FOVGameData {
        val kickerGame = gamesByUserId[kickerId] ?: throw Exception("Kicker is not in a game")
        val kickeeGame = gamesByUserId[kickeeId] ?: throw Exception("Kickee is not in a game")

        if (kickerGame != kickeeGame) {
            throw Exception("User is not in the same game")
        }

        kickerGame.kickUser(kickerId, kickeeId)
        return kickerGame.getFOV(kickerId)
    }

    fun play(userId: String, cardId: String) {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.playCard(userId, cardId)
    }

    fun vote(userId: String, cardId: String) {
        val game = gamesByUserId[userId] ?: throw Exception("User is not in a game")
        game.voteCard(userId, cardId)
    }

    fun getUserFOV(userId: String): FOVGameData? {
        return gamesByUserId[userId]?.getFOV(userId)
    }

    fun getInfoList(): List<GameInfo> {
        return gamesByName.toList().sortedWith(compareBy({it.second.name})).map { game -> game.second.getInfo() }
    }

}