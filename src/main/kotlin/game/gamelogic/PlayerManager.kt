package game.gamelogic

import model.WhiteCard

class PlayerManager(private val handSize: Int, private val deck: WhiteCardDeck) {
    private val _playersList: MutableList<MutablePlayer> = ArrayList()
    private val _players: MutableMap<String, MutablePlayer> = HashMap()

    val playersList: List<MutablePlayer> get() { return _playersList }
    val players: Map<String, MutablePlayer> get() { return _players }
    var owner: PlayerGameLogicModel? = null
        private set
    var judge: PlayerGameLogicModel? = null
        private set

    fun addUser(userId: String) {
        assertNotInGame(userId)
        val player = MutablePlayer(userId)
        if (_players.isEmpty()) {
            owner = player
        }
        _players[userId] = player
        _playersList.add(player)
    }

    fun removeUser(userId: String) {
        assertInGame(userId)
        var judgeIndex = _playersList.indexOf(judge)
        _players[userId]!!.hand.forEach { card -> deck.discardCard(card) }
        _players.remove(userId)
        _playersList.removeIf { player -> player.id == userId }
        if (_players.isEmpty()) {
            owner = null
        } else if (owner != null && userId == owner!!.id) {
            owner = _playersList[0]
        }

        if (judge != null && judge!!.id == userId) {
            if (judgeIndex == _playersList.size) {
                judgeIndex = 0
            }
            judge = if (!_playersList.isEmpty()) {
                _playersList[judgeIndex]
            } else {
                null
            }
        }
    }

    fun nextJudge() {
        if (judge == null) {
            judge = _playersList.shuffled()[0]
        } else {
            var index = _playersList.indexOf(judge) + 1
            if (index == _playersList.size) {
                index = 0
            }
            judge = _playersList[index]
        }
    }

    fun drawAllPlayersToFull() {
        _players.forEach { it.value.clearPlayedCardsAndDrawToFull() }
    }

    fun resetPlayedCards() {
        _players.forEach { it.value.resetPlayedCards() }
    }

    fun reset() {
        _players.forEach { p -> p.value.score = 0 }
        judge = null
    }

    private fun assertInGame(userId: String) {
        if (!_players.contains(userId)) {
            throw Exception("User is not in the game")
        }
    }

    private fun assertNotInGame(userId: String) {
        if (_players.contains(userId)) {
            throw Exception("User is already in the game")
        }
    }

    inner class MutablePlayer(userId: String): PlayerGameLogicModel {

        override val id: String = userId
        override var score = 0
        override val hand: List<WhiteCard>
            get() { return _hand.filter { !playedCardIds.contains(it.id) } }

        private val _hand: MutableList<WhiteCard> = ArrayList()
        private val playedCardIds: MutableList<String> = ArrayList()

        init {
            while (_hand.size < handSize) {
                _hand.add(deck.drawCard())
            }
        }

        fun incrementScore() {
            score++
        }

        fun playCard(cardId: String): WhiteCard {
            if (playedCardIds.contains(cardId)) {
                throw Exception("User has already played that card")
            }
            val card = _hand.find { it.id == cardId } ?: throw Exception("User does not have that card in their hand")
            playedCardIds.add(cardId)
            return card
        }

        // TODO - Make this method private
        fun clearPlayedCardsAndDrawToFull() {
            _hand.removeIf { playedCardIds.contains(it.id) }
            playedCardIds.clear()
            while (_hand.size < handSize) {
                _hand.add(deck.drawCard())
            }
        }

        // TODO - Make this method private
        fun resetPlayedCards() {
            playedCardIds.clear()
        }

    }
}