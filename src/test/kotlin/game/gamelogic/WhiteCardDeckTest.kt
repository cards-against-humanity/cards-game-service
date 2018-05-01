package game.gamelogic

import game.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WhiteCardDeckTest {

    private var cards: MutableList<WhiteCard> = ArrayList()
    private var deck: WhiteCardDeck

    init {
        fillCardList()
        deck = WhiteCardDeck(cards)
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
        deck = WhiteCardDeck(cards)
    }

    @Test
    fun doesNotModifyOriginalList() {
        val cardsCopy = cards.toList()
        val size = cards.size

        for (i in 1..5) {
            deck.discardCard(deck.drawCard())
        }

        assertEquals(size, cards.size)
        assert(cards.toTypedArray().contentDeepEquals(cardsCopy.toTypedArray()))
    }

    @Test
    fun cyclesThroughAllCards() {
        for (i in 1..10) {
            val cardIds: MutableSet<String> = HashSet()

            for (j in 1..100) {
                val card = deck.drawCard()
                assert(!cardIds.contains(card.id))
                cardIds.add(card.id)
                deck.discardCard(card)
            }

            for (j in 1..100) {
                assert(cardIds.contains(i.toString()))
            }
        }
    }

    @Test
    fun errorsOnEmptyDeck() {
        for (i in 1..100) {
            deck.drawCard()
        }

        val e = assertThrows(Exception::class.java, { deck.drawCard() })
        assertEquals("No cards are left in the deck", e.message)
    }

    @Test
    fun recyclesCards() {
        for (i in 1..10) {
            val cards: MutableList<WhiteCard> = ArrayList()
            for (j in 1..100) {
                cards.add(deck.drawCard())
            }

            cards.forEach { card -> deck.discardCard(card) }
        }
    }

    @Test
    fun resetDeck() {
        for (i in 1..50) {
            deck.discardCard(deck.drawCard())
        }
        deck.reset()

        cyclesThroughAllCards()
    }


    private class TestWhiteCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String
    ) : WhiteCard

}
