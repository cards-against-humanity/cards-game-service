package game

import api.CardFetcher
import model.BlackCard
import model.WhiteCard

class MockCardFetcher : CardFetcher {
    private var cardId = 0
    private val cardpacks: MutableMap<String, Pair<List<WhiteCard>, List<BlackCard>>> = HashMap()

    fun setCardpack(cardpackId: String, whiteCardTexts: List<String>, blackCardTexts: List<String>, blackCardAnswerFields: List<Int>) {
        if (blackCardTexts.size != blackCardAnswerFields.size) {
            throw Exception("Black card texts must be the same size as black card answer fields")
        }
        cardpacks[cardpackId] = Pair(
                whiteCardTexts.map { MockWhiteCard(cardId++.toString(), cardpackId, it) },
                blackCardTexts.mapIndexed { index, text -> MockBlackCard(cardId++.toString(), cardpackId, text, blackCardAnswerFields[index]) }
        )
    }

    override fun getCards(cardpackIds: List<String>): Pair<List<WhiteCard>, List<BlackCard>> {
        val whiteCards: MutableList<WhiteCard> = ArrayList()
        val blackCards: MutableList<BlackCard> = ArrayList()

        cardpackIds.forEach {
            whiteCards.addAll(cardpacks[it]!!.first)
            blackCards.addAll(cardpacks[it]!!.second)
        }

        return Pair(whiteCards, blackCards)
    }

    private data class MockWhiteCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String
    ) : WhiteCard

    private data class MockBlackCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String,
            override val answerFields: Int
    ) : BlackCard
}