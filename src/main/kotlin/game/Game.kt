package game

import api.UserFetcher
import game.gamelogic.GameLogic
import game.messaging.GameMessageModule
import game.messaging.MessageModel
import model.*
import java.security.SecureRandom
import java.util.*

class Game(val name: String, private val maxPlayers: Int, maxScore: Int, whiteCards: List<WhiteCard>, blackCards: List<BlackCard>, private val userFetcher: UserFetcher) {
    companion object {
        private const val messageModuleSize = 100
    }

    private val logic = GameLogic(maxPlayers, maxScore, whiteCards, blackCards)
    private val messages = GameMessageModule(messageModuleSize)
    private val randomizerSeed: ByteArray = ByteArray(256)

    init {
        SecureRandom().nextBytes(randomizerSeed)
    }

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

    fun startNextRound() {
        logic.startNextRound()
    }

    fun isEmpty(): Boolean {
        return logic.players.isEmpty()
    }


    fun addMessage(userId: String, text: String) {
        if (logic.players[userId] == null) {
            throw Exception("Cannot post message if you are not currently in this game")
        }
        messages.addMessage(MessageModel(userId, text))
    }


    fun getFOV(userId: String): FOVGameData {
        val users = userFetcher.getUsers(logic.playersList.map { it.id })
        val players = logic.playersList.mapIndexed { index, player -> FOVPlayer(player.id, users[index].name, player.score) }

        // TODO - Test that cardsPlayedAnonymous is null except during vote stage
        val cardsPlayedAnonymous: List<List<WhiteCard>>? = if (logic.currentBlackCard !== null && logic.whitePlayed.filter { it.key != logic.judgeId }.values.all { it.size == logic.currentBlackCard!!.answerFields }) {
            logic.whitePlayed.filter { it.key != logic.judgeId }.values.shuffled(SecureRandom(randomizerSeed))
        } else {
            null
        }

        val cardsPlayed: MutableMap<String, List<WhiteCard?>> = HashMap()

        for (entry in logic.whitePlayed) {
            if (entry.key != logic.judgeId) {
                cardsPlayed[entry.key] = if (entry.key == userId) {
                    entry.value
                } else {
                    entry.value.map { null }
                }
            }
        }

        return FOVGameData(name, maxPlayers, logic.stage, logic.players[userId]!!.hand, players, logic.judgeId, logic.ownerId!!, cardsPlayed, cardsPlayedAnonymous, logic.currentBlackCard, logic.winnerId, messages.getRecentMessages(messageModuleSize).map { Message(userFetcher.getUser(it.userId), it.text) /* TODO - Fetch users in parallel */ })
    }

    fun getInfo(): GameInfo {
        return GameInfo(name, logic.players.size, maxPlayers, userFetcher.getUser(logic.ownerId ?: throw Exception("Game is empty")))
    }

}