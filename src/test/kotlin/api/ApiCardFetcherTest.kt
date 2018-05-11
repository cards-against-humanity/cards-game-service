package api

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.net.URL
import kotlin.test.assertEquals
import com.fasterxml.jackson.databind.ObjectMapper
import model.BlackCard
import model.User
import model.WhiteCard
import org.springframework.util.SocketUtils
import java.util.*
import kotlin.collections.ArrayList


class ApiCardFetcherTest {

    companion object {
        private val port = SocketUtils.findAvailableTcpPort()
        private val mockServer: ClientAndServer = ClientAndServer.startClientAndServer(port)

        @JvmStatic
        @AfterAll
        fun stopServer() {
            mockServer.stop()
        }
    }

    private val serverUrl = URL("http://localhost:$port")
    private val cardFetcher: CardFetcher = ApiCardFetcher(serverUrl, false)

    private val cardpackId = "1234"
    private val cardpackName = "Test Cardpack"

    private val whiteCards: List<WhiteCard> = listOf(
            TestWhiteCard("1", "1", "text1"),
            TestWhiteCard("2", "1", "text2"),
            TestWhiteCard("3", "1", "text3")
    )

    private val blackCards: List<BlackCard> = listOf(
            TestBlackCard("4", "1", "text4", 1),
            TestBlackCard("5", "1", "text5", 1),
            TestBlackCard("6", "1", "text6", 1)
    )

    private val fakeCardpackId = "fake_user_id"
    private val errorCardpackId = "error_user_id"

    init {
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/cardpack/${cardpackId}")
        ).respond(
                HttpResponse.response().withBody(getCardpackJson(cardpackId, cardpackName, "1", "Owner", whiteCards, blackCards))
        )

        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/cardpack/${fakeCardpackId}")
        ).respond(
                HttpResponse.response().withStatusCode(404)
        )

        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/cardpack/${errorCardpackId}")
        ).respond(
                HttpResponse.response().withStatusCode(400)
        )
    }

    @Test
    fun getValidCards() {
        val cards = cardFetcher.getCards(cardpackId)
        cards.first.forEachIndexed { i, card -> assertEquals(whiteCards[i].id, card.id) }
        cards.second.forEachIndexed { i, card -> assertEquals(blackCards[i].id, card.id) }
    }

    @Test
    fun getInvalidCardpack() {
        val e = Assertions.assertThrows(Exception::class.java) { cardFetcher.getCards(fakeCardpackId) }
        assertEquals("Cardpack does not exist", e.message)
    }

    @Test
    fun apiError() {
        val e = Assertions.assertThrows(Exception::class.java) { cardFetcher.getCards(errorCardpackId) }
        assertEquals("An error occurred fetching cards from the api", e.message)
    }

    @Test
    fun doesNotConnectOverHttpWhenSetToSecureOnly() {
        val e = Assertions.assertThrows(Exception::class.java) { ApiUserFetcher(serverUrl, true) }
        assertEquals("Connection to API must be over https unless explicitly specified in the constructor", e.message)
    }

    private fun getCardpackJson(
            id: String,
            name: String,
            ownerId: String,
            ownerName: String, whiteCards: List<WhiteCard>,
            blackCards: List<BlackCard>): String {
        val map: MutableMap<String, Any> = HashMap()
        map.put("id", id)
        map.put("name", name)
        map.put("owner", TestUser(ownerId, ownerName) as User)
        map.put("whiteCards", whiteCards)
        map.put("blackCards", blackCards)
        map.put("createdAt", Date())

        return ObjectMapper().writeValueAsString(map)
    }

    private data class TestUser(override val id: String, override val name: String) : User

    private data class TestWhiteCard(override val id: String, override val cardpackId: String, override val text: String) : WhiteCard

    private data class TestBlackCard(override val id: String, override val cardpackId: String, override val text: String, override val answerFields: Int) : BlackCard

}