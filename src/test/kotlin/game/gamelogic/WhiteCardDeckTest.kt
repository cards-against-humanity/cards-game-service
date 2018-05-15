package game.gamelogic

import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WhiteCardDeckTest {

    private var cards: MutableList<WhiteCard> = ArrayList()
    private var deck: WhiteCardDeck

    init {
        fillCardList()
        deck = WhiteCardDeck(cards, 8)
    }

    private fun fillCardList() {
        for (i in 1..100) {
            cards.add(TestWhiteCard(i.toString(), "1", i.toString()))
        }
    }

    @BeforeEach
    fun reset() {
        cards = ArrayList()
        fillCardList()
        deck = WhiteCardDeck(cards, 8)
    }

    @Test
    fun doesNotModifyOriginalList() {
        val cardsCopy = cards.toList()
        val size = cards.size

        for (i in 1..5) {
            deck.addUser(i.toString())
        }

        assertEquals(size, cards.size)
        assert(cards.toTypedArray().contentDeepEquals(cardsCopy.toTypedArray()))
    }


    private class TestWhiteCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String
    ) : WhiteCard

}
