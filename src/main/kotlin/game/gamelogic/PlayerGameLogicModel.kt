package game.gamelogic

import model.WhiteCard

interface PlayerGameLogicModel {
    val id: String
    val score: Int
    val hand: List<WhiteCard>
}