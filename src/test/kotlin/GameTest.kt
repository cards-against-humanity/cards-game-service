import game.BlackCard
import game.Game
import game.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameTest {

    private var game: Game

    init {
        game = createGame()
    }

    private fun createGame(): Game {
        val whiteCards: MutableList<WhiteCard> = ArrayList()
        val blackCards: MutableList<BlackCard> = ArrayList()
        for (i in 1..100) {
            whiteCards.add(TestWhiteCard(i.toString(), "1", i.toString()))
        }
        for (i in 1..100) {
            blackCards.add(TestBlackCard(i.toString(), "1", i.toString(), 1))
        }
        return Game(3, whiteCards, blackCards)
    }

    @BeforeEach
    fun reset() {
        game = createGame()
    }

    @Test
    fun startAndStopWithoutError() {
        game.join("1")
        game.join("2")
        game.join("3")

        game.start("1")
        game.stop("1")
    }

    @Test
    fun errorsWhenStoppingGameThatIsNotRunning() {
        game.join("1")
        game.join("2")
        game.join("3")

        val e = assertThrows(Exception::class.java, { game.stop("1") })
        assertEquals("Game is not running", e.message)
    }

    @Test
    fun errorsWhenStartingGameThatIsAlreadyRunning() {
        game.join("1")
        game.join("2")
        game.join("3")

        game.start("1")
        val e = assertThrows(Exception::class.java, { game.start("1") })
        assertEquals("Game is already running", e.message)
    }

    @Test
    fun cannotAddSameUserTwice() {
        game.join("1")

        val e = assertThrows(Exception::class.java, { game.join("1") })
        assertEquals("User is already in the game", e.message)
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
