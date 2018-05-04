package model

interface FOVPlayer {
    val id: String
    val name: String
    val score: Int
    val hand: List<WhiteCard>
}