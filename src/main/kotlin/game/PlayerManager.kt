package game

class PlayerManager {
    private val _playersList: MutableList<InternalPlayer> = ArrayList()
    private val _players: MutableMap<String, InternalPlayer> = HashMap()

    val players: Map<String, Player> get() { return _players }
    var owner: Player? = null
        private set
    var judge: Player? = null
        private set

    fun addUser(userId: String) {
        assertNotInGame(userId)
        val player = InternalPlayer(userId)
        if (_players.isEmpty()) {
            owner = player
        }
        _players[userId] = player
        _playersList.add(player)
    }

    fun removeUser(userId: String) {
        assertInGame(userId)
        _players.remove(userId)
        _playersList.removeIf { player -> player.id == userId }
        if (_players.isEmpty()) {
            owner = null
        } else if (owner != null && userId == owner!!.id) {
            owner = _playersList[0]
        }
    }

    fun nextJudge() {
        // TODO - Implement and test
    }

    fun resetScores() {
        _players.forEach { p -> p.value.score = 0 }
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

    private class InternalPlayer(userId: String): Player {

        override val id: String = userId
        override var score = 0
        override val hand: MutableList<WhiteCard> = ArrayList()

        init {
        }

        override fun incrementScore() {
            score++
        }

    }
}