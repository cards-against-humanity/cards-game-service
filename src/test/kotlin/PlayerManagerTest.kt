import game.*
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

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

    @Test
    fun removeUser() {
        playerManager.addUser("1")
        playerManager.removeUser("1")
        val player = playerManager.players["1"]
        assertNull(player)
    }

    @Test
    fun removeNonExistentUser() {
        val e = assertThrows(Exception::class.java, { playerManager.removeUser("1") })
        assertEquals("User is not in the game", e.message)
    }

    @Test
    fun ownerIsNullOnCreation() {
        assertNull(playerManager.owner)
    }

    @Test
    fun ownerIsAssignedToFirstUser() {
        playerManager.addUser("1")
        val owner = playerManager.owner
        assertNotNull(owner)
        assertEquals("1", owner!!.id)
    }

    @Test
    fun ownerIsUnassignedWhenAllUsersLeave() {
        playerManager.addUser("1")
        playerManager.removeUser("1")
        assertNull(playerManager.owner)
    }

    @Test
    fun ownerIsReassignedWhenUserLeaves() {
        playerManager.addUser("1")
        playerManager.addUser("2")
        playerManager.addUser("3")
        playerManager.addUser("4")
        assertEquals("1", playerManager.owner!!.id)
        playerManager.removeUser("1")
        assertEquals("2", playerManager.owner!!.id)
        playerManager.removeUser("2")
        assertEquals("3", playerManager.owner!!.id)
        playerManager.removeUser("3")
        assertEquals("4", playerManager.owner!!.id)
    }

}