package model

import model.WhiteCard

interface Player {
    val id: String
    val score: Int
    val hand: List<WhiteCard>
}