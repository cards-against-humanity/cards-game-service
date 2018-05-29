package model

import game.gamelogic.GameLogic
import game.Message

data class FOVGameData(
        val name: String,
        val maxPlayers: Int,
        val stage: GameLogic.GameStage,
        val hand: List<WhiteCard>,
        val players: List<FOVPlayer>,
        val judgeId: String?,
        val ownerId: String,
        val whitePlayed: Map<String, List<WhiteCard?>>,
        val currentBlackCard: BlackCard?,
        val messages: List<Message>
)