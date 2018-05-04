package game.gamelogic

import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class PlayerManagerTest {

    val handSize = 4
    val deckSize = 1000
    var deck: WhiteCardDeck = WhiteCardDeck(listOf())
    var playerManager = PlayerManager(handSize, deck)

    @BeforeEach
    fun reset() {
        val cards = ArrayList<WhiteCard>()
        for (i in 1..deckSize) {
            cards.add(TestWhiteCard(i.toString(), "1", i.toString()))
        }
        deck = WhiteCardDeck(cards)
        playerManager = PlayerManager(handSize, deck)
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
        val players: MutableList<PlayerGameLogicModel> = ArrayList()
        for (i in 1..100) {
            playerManager.addUser(i.toString())
            val player = playerManager.players[i.toString()]!!
            for (j in 1..i) {
                player.incrementScore()
            }
            players.add(player)
        }
        playerManager.reset()
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

    @Test
    fun nullJudgeByDefault() {
        playerManager.addUser("1")
        assertNull(playerManager.judge)
    }

    @Test
    fun assignJudge() {
        playerManager.addUser("1")
        playerManager.nextJudge()
        assertNotNull(playerManager.judge)
        assertEquals("1", playerManager.judge!!.id)
    }

    @Test
    fun cycleThroughJudgesWithoutError() {
        for (i in 1..20) {
            playerManager.addUser(i.toString())
        }
        for (i in 1..200) {
            playerManager.nextJudge()
        }
    }

    @Test
    fun cycleThroughJudgesLinearly() {
        for (i in 1..20) {
            playerManager.addUser(i.toString())
        }

        for (i in 1..10) {
            val judgeCounts: MutableMap<String, Int> = HashMap()
            for (j in 1..20) {
                playerManager.nextJudge()
                if (judgeCounts[playerManager.judge!!.id] == null) {
                    judgeCounts[playerManager.judge!!.id] = 1
                } else {
                    judgeCounts[playerManager.judge!!.id]!!.inc()
                }
            }

            for (j in 1..20) {
                assertEquals(1, judgeCounts[i.toString()])
            }
        }
    }

    @Test
    fun resetJudge() {
        playerManager.addUser("1")
        playerManager.nextJudge()
        playerManager.reset()
        assertNull(playerManager.judge)
    }

    @Test
    fun reassignJudgeWhenCurrentJudgeLeaves() {
        playerManager.addUser("1")
        playerManager.addUser("2")
        playerManager.addUser("3")
        playerManager.addUser("4")
        playerManager.nextJudge()
        val judgeId = playerManager.judge!!.id
        playerManager.removeUser(judgeId)
        assertNotEquals(judgeId, playerManager.judge!!.id)
    }

    @Test
    fun removeJudgesOneAtATime() {
        for (i in 1..10) {
            playerManager.addUser(i.toString())
        }
        playerManager.nextJudge()
        for (i in 1..9) {
            val judgeId = playerManager.judge!!.id
            playerManager.removeUser(judgeId)
            assertNotEquals(judgeId, playerManager.judge!!.id)
        }
        playerManager.removeUser(playerManager.judge!!.id)
        assertNull(playerManager.judge)
    }

    @Test
    fun addCardsToPlayerHands() {
        playerManager.addUser("1")
        assertEquals(handSize, playerManager.players["1"]!!.hand.size)
    }

    @Test
    fun playingCardsDrawsNewCardsAutomatically() {
        playerManager.addUser("1")
        val player = playerManager.players["1"]!!
        player.playCard(player.hand[0].id)
        assertEquals(handSize, playerManager.players["1"]!!.hand.size)
    }

    @Test
    fun discardPlayerHandWhenLeavingGame() {
        for (i in 1..(deckSize * 10)) {
            playerManager.addUser("1")
            playerManager.removeUser("1")
        }
    }

    private class TestWhiteCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String
    ) : WhiteCard

}