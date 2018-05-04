package model

data class FOVGameData(
        val name: String,
        val maxPlayers: Int,
        val hand: List<WhiteCard>,
        val players: List<FOVPlayer>,
        val judgeId: String?,
        val ownerId: String,
        val whitePlayed: Map<String, List<WhiteCard?>>,
        val currentBlackCard: BlackCard?
)