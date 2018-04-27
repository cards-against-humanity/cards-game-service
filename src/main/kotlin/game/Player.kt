package game

interface Player {
    val id: String
    val score: Int
    val hand: List<WhiteCard>

    fun incrementScore()
}