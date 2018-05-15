package game.gamelogic

import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assert
import kotlin.test.assertEquals

class WhiteCardDeckTest {

    private val handSize = 8
    private var cards: MutableList<WhiteCard> = ArrayList()
    private var deck: WhiteCardDeck

    init {
        fillCardList()
        deck = WhiteCardDeck(cards, handSize)
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

    @Test
    fun canAddUsersWithoutError() {
        for (i in 1..10) {
            deck.addUser(i.toString())
        }
    }

    @Test
    fun keepsMapKeysUpToDate() {
        assertEquals(0, deck.userHands.size)
        assertEquals(0, deck.whitePlayed.size)
        deck.addUser("1")
        assertEquals(1, deck.userHands.size)
        assertEquals(1, deck.whitePlayed.size)
        assertEquals(handSize, deck.userHands["1"]!!.size)
        assertEquals(0, deck.whitePlayed["1"]!!.size)
        deck.addUser("2")
        assertEquals(2, deck.userHands.size)
        assertEquals(2, deck.whitePlayed.size)
        assertEquals(handSize, deck.userHands["2"]!!.size)
        assertEquals(0, deck.whitePlayed["2"]!!.size)
        deck.removeUser("1")
        deck.removeUser("2")
        assertEquals(0, deck.userHands.size)
        assertEquals(0, deck.whitePlayed.size)
    }

    @Test
    fun addsToPlayedCardsMap() {
        deck.addUser("1")

        val playedIds = deck.userHands["1"]!!.subList(0, 4).map { it.id }
        deck.playCards("1", playedIds)
        assertEquals(4, deck.whitePlayed["1"]!!.size)
        assert(deck.whitePlayed["1"]!!.map { it.id }.containsAll(playedIds))
    }

    @Test
    fun removesCardsFromHandWhenPlayingThem() {
        deck.addUser("1")

        val playedIds = deck.userHands["1"]!!.subList(0, 4).map { it.id }
        deck.playCards("1", playedIds)
        assertEquals(handSize - 4, deck.userHands["1"]!!.size)
    }

    @Test
    fun maintainsCardOrderWhenRevertingPlayedCards() {
        deck.addUser("1")

        val initialHand = deck.userHands["1"]!!.toList()

        deck.playCards("1", deck.userHands["1"]!!.subList(0, 4).map { it.id })
        deck.revertPlayedCards()
        assertEquals(handSize, deck.userHands["1"]!!.size)
        deck.userHands["1"]!!.forEachIndexed { index, card -> assertEquals(initialHand[index].id, card.id) }
    }


    private class TestWhiteCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String
    ) : WhiteCard

}
