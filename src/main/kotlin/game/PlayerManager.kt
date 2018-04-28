package game

class PlayerManager {
    private val _playersList: MutableList<InternalPlayer> = ArrayList()
    private val _players: MutableMap<String, InternalPlayer> = HashMap()

    val players: Map<String, Player> get() { return _players }
    var judge: Player? = null
        private set

    fun containsUser(userId: String): Boolean {
        _players.forEach { player ->
            if (player.value.id == userId) {
                return true
            }
        }
        return false
    }

    fun addUser(userId: String) {
        assertNotInGame(userId)
        val player = InternalPlayer(userId)
        _players[userId] = player
        _playersList.add(player)
    }

    fun removeUser(userId: String) {
        assertInGame(userId)
        _players.remove(userId)
    }

    fun resetScores() {
        _players.forEach { p -> p.value.score = 0 }
    }

    private fun assertInGame(userId: String) {
        if (!containsUser(userId)) {
            throw Exception("User is not in the game")
        }
    }

    private fun assertNotInGame(userId: String) {
        if (containsUser(userId)) {
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