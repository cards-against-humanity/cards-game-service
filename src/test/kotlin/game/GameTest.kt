package game

import api.UserFetcher
import model.BlackCard
import model.User
import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameTest {

    private val gameName = "game_name"
    private val maxPlayers = 4
    private val maxScore = 6

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
        return Game(gameName, maxPlayers, maxScore, whiteCards, blackCards, fetcher)
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
    fun fovIncludesSelfInPlayerList() {
        game.join("1")
        game.join("2")
        assertNotNull(game.getFOV("1").players.find { p -> p.id == "1" })
        assertNotNull(game.getFOV("2").players.find { p -> p.id == "2" })
    }

    @Test
    fun fowIncludesAllPlayedCardsByUserOnceRoundIsOver() {
        val userIds: MutableList<String> = mutableListOf("1", "2", "3", "4")

        userIds.forEach { game.join(it) }
        game.start(userIds[0])

        userIds.forEach {
            val fov = game.getFOV(it)
            if (fov.judgeId != it) {
                game.playCard(it, fov.hand.subList(0, fov.currentBlackCard!!.answerFields).map { it.id })
            }
        }

        val judgeFov = game.getFOV(game.getFOV(userIds[0]).judgeId!!)

        game.voteCard(judgeFov.judgeId!!, judgeFov.whitePlayedAnonymous!![0][0].id)

        val nonJudgeFov = game.getFOV(userIds.find { it != game.getFOV(it).judgeId }!!)

        assertEquals(userIds.size - 1, nonJudgeFov.whitePlayed.size)
        nonJudgeFov.whitePlayed.values.forEach {
            assertEquals(judgeFov.currentBlackCard!!.answerFields, it.size)
            it.forEach { assertNotNull(it) }
        }
    }

    @Test
    fun fowShowsAllPlayedCardsByUserAsNullBeforeJudgeVotes() {
        val userIds: MutableList<String> = mutableListOf("1", "2", "3", "4")

        userIds.forEach { game.join(it) }
        game.start(userIds[0])

        userIds.forEach {
            val fov = game.getFOV(it)
            if (fov.judgeId != it) {
                game.playCard(it, fov.hand.subList(0, fov.currentBlackCard!!.answerFields).map { it.id })
            }
        }

        val nonJudgeUserId = userIds.find { it != game.getFOV(it).judgeId }!!
        val nonJudgeFov = game.getFOV(nonJudgeUserId)

        assertEquals(userIds.size - 1, nonJudgeFov.whitePlayed.size)
        nonJudgeFov.whitePlayed.forEach {
            assertEquals(nonJudgeFov.currentBlackCard!!.answerFields, it.value.size)
            if (it.key == nonJudgeUserId) {
                it.value.forEach { assertNotNull(it) }
            } else {
                it.value.forEach { assertNull(it) }
            }
        }
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