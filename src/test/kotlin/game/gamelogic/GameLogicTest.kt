package game.gamelogic

import model.BlackCard
import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameLogicTest {

    private var game: GameLogic

    init {
        game = createGame()
    }

    private fun createGame(): GameLogic {
        val whiteCards: MutableList<WhiteCard> = ArrayList()
        val blackCards: MutableList<BlackCard> = ArrayList()
        for (i in 1..100) {
            whiteCards.add(TestWhiteCard(i.toString(), "1", i.toString()))
        }
        for (i in 1..100) {
            blackCards.add(TestBlackCard(i.toString(), "1", i.toString(), 1))
        }
        return GameLogic(4, whiteCards, blackCards)
    }

    @BeforeEach
    fun reset() {
        game = createGame()
    }

    private fun addUsers() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
    }

    private fun addUsersAndStartGame() {
        addUsers()
        game.start("1")
    }

    @Test
    fun startAndStopWithoutError() {
        addUsersAndStartGame()
        game.stop("1")
    }

    @Test
    fun errorsWhenStoppingGameThatIsNotRunning() {
        addUsers()
        val e = assertThrows(Exception::class.java, { game.stop("1") })
        assertEquals("Game is not running", e.message)
    }

    @Test
    fun errorsWhenStartingGameThatIsAlreadyRunning() {
        addUsersAndStartGame()
        val e = assertThrows(Exception::class.java, { game.start("1") })
        assertEquals("Game is already running", e.message)
    }

    @Test
    fun cannotAddSameUserTwice() {
        game.join("1")
        val e = assertThrows(Exception::class.java, { game.join("1") })
        assertEquals("User is already in the game", e.message)
    }

    @Test
    fun cannotVoteDuringPlayPhase() {
        addUsersAndStartGame()
        val e = assertThrows(Exception::class.java, { game.voteCard(game.judgeId!!, "") })
        assertEquals("Cannot vote for a card", e.message)
    }

    @Test
    fun gameSetToPlayPhaseWhenStarted() {
        addUsersAndStartGame()
        assertEquals(GameLogic.GameStage.PLAY_PHASE, game.stage)
    }

    @Test
    fun currentBlackCardIsNullWhenGameIsNotRunning() {
        assertNull(game.currentBlackCard)
        addUsersAndStartGame()
        game.stop(game.ownerId!!)
        assertNull(game.currentBlackCard)
    }

    @Test
    fun currentBlackCardIsNotNullWhenGameIsRunning() {
        addUsersAndStartGame()
        assertNotNull(game.currentBlackCard)
    }

    @Test
    fun syncPlayedCardsListsWithUsersInGame() {
        assertTrue(game.whitePlayed.isEmpty())
        game.join("1")
        assertNotNull(game.whitePlayed["1"])
        game.leave("1")
        assertTrue(game.whitePlayed.isEmpty())
    }

    @Test
    fun addsPlayedCards() {
        addUsersAndStartGame()
        val player = game.playersList.find { player -> player.id != game.judgeId }!!
        game.playCard(player.id, player.hand[0].id)
        assertEquals(1, game.whitePlayed[player.id]!!.size)
    }

    @Test
    fun stopsGameWhenPlayerLeavesAndNotEnoughPlayersAreRemaining() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.start("1")
        game.leave("3")
        val e = assertThrows(Exception::class.java) { game.stop("1") }
        assertEquals("Game is not running", e.message)
    }

    private class TestWhiteCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String
    ) : WhiteCard

    private class TestBlackCard(
            override val id: String,
            override val cardpackId: String,
            override val text: String,
            override val answerFields: Int
    ) : BlackCard

}
