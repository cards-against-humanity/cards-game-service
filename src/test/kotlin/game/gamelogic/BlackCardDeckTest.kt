package game.gamelogic

import model.BlackCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class BlackCardDeckTest {

    private var cards: MutableList<BlackCard> = ArrayList()
    private var deck: BlackCardDeck

    init {
        fillCardList()
        deck = BlackCardDeck(cards)
    }

    private fun fillCardList() {
        for (i in 1..100) {
            cards.add(TestBlackCard(i.toString(), "1", i.toString(), 1))
        }
    }

    @BeforeEach
    fun reset() {
        cards = ArrayList()
        fillCardList()
        deck = BlackCardDeck(cards)
    }

    @Test
    fun setsNewCard() {
        val id = deck.currentCard.id

        assertEquals(deck.currentCard.id, id)
        deck.setNewCard()
        assertNotEquals(deck.currentCard.id, id)
    }

    @Test
    fun doesNotCrashWhenSettingBeyondDeckSize() {
        for (i in 1..100000) {
            deck.setNewCard()
        }
    }

    @Test
    fun doesNotModifyOriginalList() {
        val cardsCopy = cards.toList()
        val size = cards.size

        for (i in 1..5) {
            deck.setNewCard()
        }

        assertEquals(size, cards.size)
        assert(cards.toTypedArray().contentDeepEquals(cardsCopy.toTypedArray()))
    }

    @Test
    fun cyclesThroughAllCards() {
        for (i in 1..10) {
            val cardIds: MutableSet<String> = HashSet()

            for (j in 1..100) {
                assert(!cardIds.contains(deck.currentCard.id))
                cardIds.add(deck.currentCard.id)
                deck.setNewCard()
            }

            for (j in 1..100) {
                assert(cardIds.contains(i.toString()))
            }
        }
    }

    @Test
    fun resetDeck() {
        for (i in 1..50) {
            deck.setNewCard()
        }
        deck.reset()

        cyclesThroughAllCards()
    }

    @Test
    fun errorsWithEmptyCardList() {
        val e = assertThrows(IllegalArgumentException::class.java, { BlackCardDeck(arrayListOf()) })
        assertEquals("Card list cannot be empty", e.message)
    }


    private class TestBlackCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String,
            override val answerFields: Int
    ) : BlackCard

}
