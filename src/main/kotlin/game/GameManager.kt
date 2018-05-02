package game

import api.*
import game.gamelogic.GameLogic
import model.FOVGameData
import model.GameInfo

class GameManager(cardFetcher: CardFetcher) {

    private val gamesByName: MutableMap<String, Game> = HashMap()
    private val gamesByUserId: MutableMap<String, Game> = HashMap()

    fun createGame(userId: String, gameName: String, cardpackIds: List<String>): FOVGameData {
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


    fun getUserFOV(userId: String): FOVGameData? {
        TODO()
    }

    fun getInfoList(): List<GameInfo> {
        TODO()
    }

}