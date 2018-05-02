package game

import model.FOVGameData
import model.GameInfo

interface Game {
    fun start(userId: String)
    fun stop(userId: String)
    fun join(userId: String)
    fun leave(userId: String)
    fun kickUser(kickerId: String, kickeeId: String)
    fun playCard(userId: String, cardId: String)
    fun voteCard(userId: String, cardId: String)

    fun getFOV(userId: String): FOVGameData
    fun getInfo(): GameInfo
}