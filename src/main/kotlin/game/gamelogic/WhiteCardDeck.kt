package game.gamelogic

import game.WhiteCard

class WhiteCardDeck(cards: List<WhiteCard>) {

    private var drawPile: MutableList<WhiteCard> = cards.toMutableList()
    private var discardPile: MutableList<WhiteCard> = ArrayList()

    init {
        drawPile.shuffle()
    }

    fun drawCard(): WhiteCard {
        if (drawPile.isEmpty() && discardPile.isEmpty()) {
            throw Exception("No cards are left in the deck")
        }

        if (drawPile.isEmpty()) {
            reset()
        }
        val card = drawPile[0]
        drawPile = drawPile.drop(1).toMutableList()
        return card
    }

    fun discardCard(card: WhiteCard) {
        discardPile.add(card)
    }

    fun reset() {
        drawPile.addAll(discardPile)
        discardPile = ArrayList()
        drawPile.shuffle()
    }

}