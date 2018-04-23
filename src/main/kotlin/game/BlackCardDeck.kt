package game

import java.lang.IllegalArgumentException

class BlackCardDeck(cards: List<BlackCard>) {

    var currentCard: BlackCard private set
    private var drawPile: MutableList<BlackCard> = cards as MutableList
    private var discardPile: MutableList<BlackCard> = ArrayList()

    init {
        if (cards.isEmpty()) {
            throw IllegalArgumentException("Card list cannot be empty")
        }
        shuffleDrawPile()
        currentCard = drawPile[0]
        drawPile = drawPile.drop(1).toMutableList()
    }

    fun setNewCard() {
        if (drawPile.isEmpty()) {
            reset()
        } else {
            discardPile.add(currentCard)
            currentCard = drawPile[0]
            drawPile = drawPile.drop(1).toMutableList()
        }
    }

    fun reset() {
        drawPile.addAll(discardPile)
        drawPile.add(currentCard)
        discardPile = ArrayList()
        shuffleDrawPile()
        currentCard = drawPile[0]
        drawPile = drawPile.drop(1).toMutableList()
    }

    private fun shuffleDrawPile() {
        drawPile.shuffle()
    }

}