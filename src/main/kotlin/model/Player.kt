package model

interface Player : HiddenPlayer {
interface Player {
    val id: String
    val score: Int
    val hand: List<WhiteCard>
}