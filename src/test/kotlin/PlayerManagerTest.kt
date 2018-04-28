import game.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlayerManagerTest {

    var playerManager = PlayerManager()

    @BeforeEach
    fun reset() {
        playerManager = PlayerManager()
    }

    @Test
    fun accessPlayerObject() {
        playerManager.addUser("1")
        val player = playerManager.players["1"]
        assertNotNull(player)
    }

    @Test
    fun incrementPlayerScore() {
        playerManager.addUser("1")
        val player = playerManager.players["1"]!!
        assertEquals(0, player.score)
        player.incrementScore()
        assertEquals(1, player.score)
    }

    @Test
    fun resetScores() {
        val players: MutableList<Player> = ArrayList()
        for (i in 1..100) {
            playerManager.addUser(i.toString())
            val player = playerManager.players[i.toString()]!!
            for (j in 1..i) {
                player.incrementScore()
            }
            players.add(player)
        }
        playerManager.resetScores()
        players.forEach { player ->
            assertEquals(0, player.score)
        }
    }

    @Test
    fun addDuplicateUser() {
        playerManager.addUser("1")
        val e = assertThrows(Exception::class.java, { playerManager.addUser("1") })
        assertEquals("User is already in the game", e.message)
    }

}