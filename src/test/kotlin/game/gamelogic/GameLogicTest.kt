package game.gamelogic

import model.BlackCard
import model.WhiteCard
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class GameLogicTest {

    private val maxScore = 6
    private val maxPlayers = 4
    private var game: GameLogic

    init {
        game = createGame()
    }

    private fun generateWhiteCards(count: Int): List<WhiteCard> {
        val cards: MutableList<WhiteCard> = ArrayList()
        for (i in 1..count) {
            cards.add(TestWhiteCard(i.toString(), "1", i.toString()))
        }
        return cards
    }

    private fun generateBlackCards(count: Int): List<BlackCard> {
        val cards: MutableList<BlackCard> = ArrayList()
        for (i in 1..count) {
            cards.add(TestBlackCard(i.toString(), "1", i.toString(), 1))
        }
        return cards
    }

    private fun createGame(): GameLogic {
        return GameLogic(maxPlayers, maxScore, generateWhiteCards(100), generateBlackCards(100))
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

    private fun playCardsForAllUsers() {
        game.players.values.forEach {
            if (it.id != game.judgeId) {
                game.playCards(it.id, it.hand.subList(0, game.currentBlackCard!!.answerFields).map { it.id })
            }
        }
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
        game.playCards(player.id, player.hand.subList(0, game.currentBlackCard!!.answerFields).map { it.id })
        assertEquals(game.currentBlackCard!!.answerFields, game.whitePlayed[player.id]!!.size)
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

    @Test
    fun gameEntersJudgePhaseOnceAllUsersHavePlayed() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.start("1")

        playCardsForAllUsers()

        assertEquals(GameLogic.GameStage.JUDGE_PHASE, game.stage)
    }

    @Test
    fun judgeCanVoteAfterAllUsersHavePlayed() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.start("1")

        playCardsForAllUsers()

        val set = game.whitePlayed.toList().find { it.first != game.judgeId }!!
        val winningCardId = set.second[0].id

        game.voteCard(game.judgeId!!, winningCardId)
    }

    @Test
    fun incrementsScoreWhenCardIsVotedFor() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.start("1")

        playCardsForAllUsers()

        val set = game.whitePlayed.toList().find { it.first != game.judgeId }!!
        val winnerId = set.first
        val winningCardId = set.second[0].id

        game.voteCard(game.judgeId!!, winningCardId)
        assertEquals(1, game.players[winnerId]!!.score)
    }

    @Test
    fun returnsPlayedCardsToUsersHandsIfJudgeLeavesDuringPlayPhase() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start("1")

        val nonJudgeUserId = game.players.values.map { it.id }.find { it != game.judgeId }!!

        val initialHand = game.players[nonJudgeUserId]!!.hand.toList()
        game.playCards(nonJudgeUserId, initialHand.subList(0, game.currentBlackCard!!.answerFields).map { it.id })
        game.leave(game.judgeId!!)

        val currentHand = game.players[nonJudgeUserId]!!.hand

        assertEquals(initialHand.size, currentHand.size)
        currentHand.forEachIndexed { index, card ->
            assertEquals(initialHand[index].id, card.id)
        }
    }

    @Test
    fun returnsPlayedCardsToUsersHandsIfJudgeLeavesDuringVotePhase() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start("1")

        val nonJudgeUserId = game.players.values.map { it.id }.find { it != game.judgeId }!!

        val initialHand = game.players[nonJudgeUserId]!!.hand.toList()

        playCardsForAllUsers()

        game.leave(game.judgeId!!)

        val currentHand = game.players[nonJudgeUserId]!!.hand

        assertEquals(initialHand.size, currentHand.size)
        currentHand.forEachIndexed { index, card ->
            assertEquals(initialHand[index].id, card.id)
        }
    }

    @Test
    fun doesNotAutomaticallyRedrawWhenCardIsPlayed() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.start("1")

        val nonJudgeUserId = game.players.values.map { it.id }.find { it != game.judgeId }!!
        val initialHand = game.players[nonJudgeUserId]!!.hand.toList()
        game.playCards(nonJudgeUserId, initialHand.subList(0, game.currentBlackCard!!.answerFields).map { it.id })
        val currentHand = game.players[nonJudgeUserId]!!.hand
        assertEquals(initialHand.size, currentHand.size + 1)
    }

    @Test
    fun endRoundWhenJudgeLeavesDuringVoteProcess() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start("1")

        playCardsForAllUsers()

        game.leave(game.judgeId!!)

        assertEquals(GameLogic.GameStage.ROUND_END_PHASE, game.stage)
    }

    @Test
    fun gracefullyContinuesWhenJudgeLeavesDuringVotePhase() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start("1")

        playCardsForAllUsers()
        game.leave(game.judgeId!!)
        game.startNextRound()
        playCardsForAllUsers()

        assertEquals(GameLogic.GameStage.JUDGE_PHASE, game.stage)
    }

    @Test
    fun cannotPlayMoreCardsThanCurrentBlackCardAllows() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.start("1")

        val nonJudgeUserId = game.players.values.map { it.id }.find { it != game.judgeId }!!
        val initialHand = game.players[nonJudgeUserId]!!.hand.toList()
        val e = assertThrows(Exception::class.java) { game.playCards(nonJudgeUserId, initialHand.subList(0, game.currentBlackCard!!.answerFields + 1).map { it.id }) }
        assertEquals("Must play exactly ${game.currentBlackCard!!.answerFields} cards", e.message)
    }

    @Test
    fun cyclesThroughMultipleRoundsWithoutError() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")

        for (i in 1..1000) {
            if (game.stage == GameLogic.GameStage.NOT_RUNNING) {
                game.start(game.ownerId!!)
            }
            playCardsForAllUsers()
            val nonJudgePlayerId = game.playersList.find { it.id != game.judgeId }!!.id
            val judgeId = game.judgeId!!
            val whitePlayedNonJudge = game.whitePlayed[nonJudgePlayerId]!!
            val nonPlayedCardId = whitePlayedNonJudge[0].id
            game.voteCard(judgeId, nonPlayedCardId)
            if (game.stage == GameLogic.GameStage.ROUND_END_PHASE) {
                game.startNextRound()
            }
        }
    }

    @Test
    fun winnerIdIsNullForNewGame() {
        assertNull(game.winnerId)
    }

    @Test
    fun winnerIdIsCorrectWhenGameEnds() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start(game.ownerId!!)

        var winnerId = ""

        while (game.stage != GameLogic.GameStage.NOT_RUNNING) {
            playCardsForAllUsers()
            val nonJudgePlayerId = game.playersList.find { it.id != game.judgeId }!!.id
            winnerId = nonJudgePlayerId
            val judgeId = game.judgeId!!
            val whitePlayedNonJudge = game.whitePlayed[nonJudgePlayerId]!!
            val nonPlayedCardId = whitePlayedNonJudge[0].id
            game.voteCard(judgeId, nonPlayedCardId)
            if (game.stage == GameLogic.GameStage.ROUND_END_PHASE) {
                game.startNextRound()
            }
        }

        assertEquals(winnerId, game.winnerId)
    }

    @Test
    fun winnerIdIsNullAfterFinishingGameAndRestarting() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start(game.ownerId!!)

        while (game.stage != GameLogic.GameStage.NOT_RUNNING) {
            playCardsForAllUsers()
            val nonJudgePlayerId = game.playersList.find { it.id != game.judgeId }!!.id
            val judgeId = game.judgeId!!
            val whitePlayedNonJudge = game.whitePlayed[nonJudgePlayerId]!!
            val nonPlayedCardId = whitePlayedNonJudge[0].id
            game.voteCard(judgeId, nonPlayedCardId)
            if (game.stage == GameLogic.GameStage.ROUND_END_PHASE) {
                game.startNextRound()
            }
        }

        game.start(game.ownerId!!)

        assertNull(game.winnerId)
    }

    @Test
    fun gameStopsAfterMaxScoreIsReached() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start(game.ownerId!!)

        var highestScore = 0

        while (game.stage != GameLogic.GameStage.NOT_RUNNING) {
            playCardsForAllUsers()
            val nonJudgePlayerId = game.playersList.find { it.id != game.judgeId }!!.id
            val judgeId = game.judgeId!!
            val whitePlayedNonJudge = game.whitePlayed[nonJudgePlayerId]!!
            val nonPlayedCardId = whitePlayedNonJudge[0].id
            highestScore = Math.max(highestScore, game.players[nonJudgePlayerId]!!.score + 1)
            game.voteCard(judgeId, nonPlayedCardId)
            if (game.stage == GameLogic.GameStage.ROUND_END_PHASE) {
                game.startNextRound()
            }
        }

        assertEquals(maxScore, highestScore)
    }

    @Test
    fun changesJudgeWhenStartingNewRound() {
        game.join("1")
        game.join("2")
        game.join("3")
        game.join("4")
        game.start(game.ownerId!!)

        var judgeId = game.judgeId!!

        playCardsForAllUsers()
        val nonJudgePlayerId = game.playersList.find { it.id != game.judgeId }!!.id
        val whitePlayedNonJudge = game.whitePlayed[nonJudgePlayerId]!!
        val nonPlayedCardId = whitePlayedNonJudge[0].id
        game.voteCard(judgeId, nonPlayedCardId)

        assertEquals(judgeId, game.judgeId)
        game.startNextRound()
        assertNotEquals(judgeId, game.judgeId)
        assertNotNull(game.judgeId)
    }

    @Test
    fun cannotCreateGameWithMaxScoreOfZero() {
        val e = assertThrows(Exception::class.java) { GameLogic(maxPlayers, 0, generateWhiteCards(100), generateBlackCards(100)) }
        assertEquals("Max score must be a positive number", e.message)
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
