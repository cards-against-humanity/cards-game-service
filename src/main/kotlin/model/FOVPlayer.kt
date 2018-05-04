package model

data class FOVPlayer (
    val id: String,
    val name: String,
    val score: Int,
    val hand: List<WhiteCard>
)