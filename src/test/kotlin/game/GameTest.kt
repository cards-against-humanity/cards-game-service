package game

import api.UserFetcher
import model.BlackCard
import model.User
import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameTest {

    private val gameName = "game_name"
    private val maxPlayers = 4

    private var game: Game
    private var userFetcher: MockUserFetcher

    init {
        userFetcher = MockUserFetcher()
        game = createGame(userFetcher)
    }

    private fun createGame(fetcher: UserFetcher): Game {
        val whiteCards: MutableList<WhiteCard> = ArrayList()
        val blackCards: MutableList<BlackCard> = ArrayList()
        for (i in 1..100) {
            whiteCards.add(TestWhiteCard(i.toString(), "1", i.toString()))
        }
        for (i in 1..100) {
            blackCards.add(TestBlackCard(i.toString(), "1", i.toString(), 1))
        }
        return Game(gameName, maxPlayers, whiteCards, blackCards, fetcher)
    }

    @BeforeEach
    fun reset() {
        userFetcher = MockUserFetcher()

        for (i in 1..100) {
            userFetcher.setUser(i.toString(), "user_$i")
        }

        game = createGame(userFetcher)
    }

    private fun addUsers() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
    }




    @Test
    fun fovDoesNotIncludeSelfInPlayerList() {
        game.join("1")
        game.join("2")
        assertNull(game.getFOV("1").players.find { p -> p.id == "1" })
        assertNull(game.getFOV("2").players.find { p -> p.id == "2" })
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

    private class MockUserFetcher : UserFetcher {
        private val users: MutableMap<String, User> = HashMap()

        fun setUser(id: String, name: String) {
            users[id] = MockUser(id, name)
        }

        override fun getUsers(userIds: List<String>): List<User> {
            return userIds.map { id -> users[id]!! }
        }

        private data class MockUser(override val id: String, override val name: String) : User
    }

}