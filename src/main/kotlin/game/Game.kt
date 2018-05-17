package game

import api.UserFetcher
import game.gamelogic.GameLogic
import model.*

class Game(val name: String, private val maxPlayers: Int, maxScore: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard>, private val userFetcher: UserFetcher) {

    private val logic = GameLogic(maxPlayers, maxScore, whiteCards, blackCards)

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

    fun playCard(userId: String, cardIds: List<String>) {
        logic.playCards(userId, cardIds)
    }

    fun voteCard(userId: String, cardId: String) {
        logic.voteCard(userId, cardId)
    }

    fun isEmpty(): Boolean {
        return logic.players.isEmpty()
    }


    fun getFOV(userId: String): FOVGameData {
        val users = userFetcher.getUsers(logic.playersList.map { playerEntry -> playerEntry.id })
        val players = logic.playersList.mapIndexed { index, player -> FOVPlayer(player.id, users[index].name, player.score) }.filter { p -> p.id != userId }

        val cardsPlayed: MutableMap<String, List<WhiteCard?>> = HashMap()
        for (entry in logic.whitePlayed) {
            cardsPlayed[entry.key] = if (entry.key == userId) {
                entry.value
            } else {
                entry.value.map { _ -> null }
            }
        }
        return FOVGameData(name, maxPlayers, logic.players[userId]!!.hand, players, logic.judgeId, logic.ownerId!!, cardsPlayed, logic.currentBlackCard)
    }

    fun getInfo(): GameInfo {
        return GameInfo(name, logic.players.size, maxPlayers, userFetcher.getUser(logic.ownerId ?: throw Exception("Game is empty")))
    }

}