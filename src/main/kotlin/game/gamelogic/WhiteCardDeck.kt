package game.gamelogic

import model.WhiteCard

class WhiteCardDeck(cards: List<WhiteCard>, private val handSize: Int) {

    val userHands: Map<String, List<WhiteCard>> get() {
        val whitePlayed: MutableMap<String, List<WhiteCard>> = HashMap()
        _userHands.forEach { userId, hand -> whitePlayed[userId] = hand.filter { card -> !playedCardIds[userId]!!.contains(card.id) } }
        return whitePlayed
    }

    val whitePlayed: Map<String, List<WhiteCard>> get() {
        val whitePlayed: MutableMap<String, List<WhiteCard>> = HashMap()
        // TODO - Test line below to ensure that cards are returned in the order they are played
        playedCardIds.forEach { userId, cardIds -> whitePlayed[userId] = cardIds.map { cardId -> _userHands[userId]!!.find { it.id == cardId }!! } }
        return whitePlayed
    }

    private val drawPile: MutableList<WhiteCard> = cards.toMutableList()
    private val discardPile: MutableList<WhiteCard> = ArrayList()

    private val playedCardIds: MutableMap<String, List<String>> = HashMap()
    private val _userHands: MutableMap<String, MutableList<WhiteCard>> = HashMap()

    init {
        drawPile.shuffle()
    }

    fun addUser(userId: String) {
        if (_userHands[userId] != null) {
            throw Exception("User is already in the game")
        }

        _userHands[userId] = ArrayList()
        playedCardIds[userId] = ArrayList()

        drawUserToFull(userId)
    }

    fun removeUser(userId: String) {
        if (_userHands[userId] == null) {
            throw Exception("User has not been added")
        }

        discardPile.addAll(_userHands[userId]!!)
        _userHands.remove(userId)
        playedCardIds.remove(userId)
    }

    fun playCards(userId: String, cardIds: List<String>) {
        // TODO - Write UT that tries using duplicate card ids

        if (_userHands[userId] == null) {
            throw Exception("User has not been added")
        } else if (!_userHands[userId]!!.map { it.id }.containsAll(cardIds)) {
            throw Exception("User does not have all of those cards in their hand")
        } else if (cardIds.any { playCardId -> playedCardIds[userId]!!.contains(playCardId) }) {
            throw Exception("User has already played that card")
        } else if (!playedCardIds[userId]!!.isEmpty()) {
            throw Exception("User has already played")
        } else {
            playedCardIds[userId] = cardIds.toList()
        }
    }

    fun discardPlayedCardsAndRedraw() {
        _userHands.keys.forEach { userId ->
            val cardsToRemove = _userHands[userId]!!.filter { playedCardIds[userId]!!.contains(it.id) }
            discardPile.addAll(cardsToRemove)
            _userHands[userId]!!.removeAll(cardsToRemove)
            playedCardIds[userId] = ArrayList()
        }

        drawAllUsersToFull()
    }

    fun revertPlayedCards() {
        playedCardIds.keys.forEach { playedCardIds[it] = ArrayList() }
    }

    fun resetAndDrawNewHands() {
        _userHands.keys.forEach { userId ->
            discardPile.addAll(_userHands[userId]!!)
            _userHands[userId]!!.clear()
            playedCardIds[userId] = ArrayList()
        }

        drawAllUsersToFull()
    }

    private fun shuffleDiscardPile() {
        drawPile.addAll(discardPile)
        discardPile.clear()
        drawPile.shuffle()
    }

    private fun popCardFromDrawPile(): WhiteCard {
        if (drawPile.isEmpty() && discardPile.isEmpty()) {
            throw Exception("No cards are left in the deck")
        }

        if (drawPile.isEmpty()) {
            shuffleDiscardPile()
        }

        return drawPile.removeAt(0)
    }

    private fun drawUserToFull(userId: String) {
        val hand = _userHands[userId]!!

        while (hand.size < handSize) {
            hand.add(popCardFromDrawPile())
        }
    }

    private fun drawAllUsersToFull() {
        _userHands.keys.forEach { drawUserToFull(it) }
    }

}