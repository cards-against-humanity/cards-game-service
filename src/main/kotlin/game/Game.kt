package game

import game.gamelogic.GameLogic
import model.*

class Game(val name: String, val maxPlayers: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard>) {

    private val logic = GameLogic(maxPlayers, whiteCards, blackCards)

    fun start(userId: String) {
        logic.start(userId)
    }

    fun stop(userId: String) {
        logic.stop(userId)
    }

    fun join(userId: String) {
        logic.join(userId)
    }

    fun leave(userId: String) {
        logic.leave(userId)
    }

    fun kickUser(kickerId: String, kickeeId: String) {
        logic.kickUser(kickerId, kickeeId)
    }

    fun playCard(userId: String, cardId: String) {
        logic.playCard(userId, cardId)
    }

    fun voteCard(userId: String, cardId: String) {
        logic.voteCard(userId, cardId)
    }

    fun isEmpty(): Boolean {
        return logic.players.isEmpty()
    }


    fun getFOV(userId: String): FOVGameData {
        return InternalFOVGameData()
    }

    fun getInfo(): GameInfo {
        return GameInfo(name, logic.players.size, maxPlayers, logic.ownerId ?: throw Exception("Game is empty"))
    }

    class InternalFOVGameData : FOVGameData

}