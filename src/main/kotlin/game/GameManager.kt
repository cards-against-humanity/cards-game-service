package game

import api.*
import game.gamelogic.GameLogic

class GameManager(cardFetcher: CardFetcher) {

    private val gamesByName: MutableMap<String, Game> = HashMap()
    private val gamesByUserId: MutableMap<String, Game> = HashMap()

    fun createGame(userId: String, gameName: String, cardpackIds: List<String>): Game.FOVInfo {
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


    fun getUserFOV(userId: String): Game.FOVInfo? {
        TODO()
    }

    fun getInfoList(): List<Game.Info> {
        TODO()
    }

}