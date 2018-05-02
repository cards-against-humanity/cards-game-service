package api

import model.*

interface CardFetcher {
    fun getCards(cardpackIds: List<String>): Pair<List<WhiteCard>, List<BlackCard>>

    fun getCards(cardpackId: String): Pair<List<WhiteCard>, List<BlackCard>> {
        return getCards(listOf(cardpackId))
    }
}