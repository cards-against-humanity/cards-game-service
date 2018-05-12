package game

import junit.framework.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameManagerTest {

    private var registeredUserIds: List<String> = ArrayList()
    private var registeredCardpackIds: List<String> = ArrayList()

    private var userFetcher = MockUserFetcher()
    private var cardFetcher = MockCardFetcher()
    private var gameManager = GameManager(userFetcher, cardFetcher)

    @BeforeEach
    fun reset() {
        userFetcher = MockUserFetcher()
        cardFetcher = MockCardFetcher()

        val userIds: MutableList<String> = ArrayList()
        for (i in 1..100) {
            userIds.add(i.toString())
            userFetcher.setUser(i.toString(), "user_$i")
        }
        registeredUserIds = userIds

        val cardpackIds: MutableList<String> = ArrayList()
        for (i in 1..10) {
            cardpackIds.add(i.toString())
            cardFetcher.setCardpack(i.toString(), listOf(1..100).flatten().map { "whitecard_$it" }, listOf(1..10).flatten().map { "blackcard_$it" }, listOf(1..10).flatten().map { 1 })
        }
        registeredCardpackIds = cardpackIds

        gameManager = GameManager(userFetcher, cardFetcher)
    }



    @Test
    fun addsGameToInfoList() {
        val gameName = "game_one"
        val maxPlayers = 4
        gameManager.createGame(registeredUserIds[0], gameName, maxPlayers, listOf(registeredCardpackIds[0]))
        gameManager.joinGame(registeredUserIds[1], gameName)

        val infoList = gameManager.getInfoList()
        assertEquals(1, infoList.size)
        assertEquals(gameName, infoList[0].name)
        assertEquals(maxPlayers, infoList[0].maxPlayers)
        assertEquals(registeredUserIds[0], infoList[0].owner.id)
        assertEquals(2, infoList[0].playerCount)
    }

    @Test
    fun removesGameFromInfoListWhenAllUsersLeave() {
        val gameName = "game_one"
        gameManager.createGame(registeredUserIds[0], gameName, 4, listOf(registeredCardpackIds[0]))
        gameManager.joinGame(registeredUserIds[1], gameName)
        gameManager.joinGame(registeredUserIds[2], gameName)
        gameManager.joinGame(registeredUserIds[3], gameName)

        gameManager.leaveGame(registeredUserIds[0])
        gameManager.leaveGame(registeredUserIds[1])
        gameManager.leaveGame(registeredUserIds[2])
        gameManager.leaveGame(registeredUserIds[3])

        assertEquals(0, gameManager.getInfoList().size)
    }

    @Test
    fun removeUserFromOldGameWhenCreatingNewOne() {
        val gameOneName = "game_one"
        val gameTwoName = "game_two"
        gameManager.createGame(registeredUserIds[0], gameOneName, 4, listOf(registeredCardpackIds[0]))
        gameManager.createGame(registeredUserIds[0], gameTwoName, 4, listOf(registeredCardpackIds[0]))

        val infoList = gameManager.getInfoList()
        assertEquals(1, infoList.size)
        assertEquals(gameTwoName, infoList[0].name)
    }

    @Test
    fun removeUserFromOldGameWhenJoiningNewOne() {
        val gameOneName = "game_one"
        val gameTwoName = "game_two"
        gameManager.createGame(registeredUserIds[0], gameOneName, 4, listOf(registeredCardpackIds[0]))
        gameManager.createGame(registeredUserIds[1], gameTwoName, 4, listOf(registeredCardpackIds[0]))

        gameManager.joinGame(registeredUserIds[2], gameOneName)
        gameManager.joinGame(registeredUserIds[2], gameTwoName)

        assertEquals(1, gameManager.getInfoList().find { it.name == gameOneName }!!.playerCount)
        assertEquals(2, gameManager.getInfoList().find { it.name == gameTwoName }!!.playerCount)
    }

}