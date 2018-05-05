import api.ApiUserFetcher
import api.UserFetcher
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import java.net.URL
import kotlin.test.assertEquals

class ApiUserFetcherTest {
    companion object {
        private const val port = 8080
        private val mockServer: ClientAndServer = ClientAndServer.startClientAndServer(port)

        @JvmStatic
        @AfterAll
        fun stopServer() {
            mockServer.stop()
        }
    }

    private val serverUrl = URL("http://localhost:$port")
    private val userFetcher: UserFetcher = ApiUserFetcher(serverUrl, false)

    private val userId = "1234"
    private val userName = "Tommy"

    private val fakeUserId = "fake_user_id"
    private val errorUserId = "error_user_id"

    init {
        mockServer.`when`(
                request()
                        .withMethod("GET")
                        .withPath("/user/${userId}")
        ).respond(
                response().withBody("{\"id\": \"${userId}\", \"name\": \"${userName}\"}")
        )

        mockServer.`when`(
                request()
                        .withMethod("GET")
                        .withPath("/user/${fakeUserId}")
        ).respond(
                response().withStatusCode(404)
        )

        mockServer.`when`(
                request()
                        .withMethod("GET")
                        .withPath("/user/${errorUserId}")
        ).respond(
                response().withStatusCode(400)
        )
    }

    @Test
    fun getValidUser() {
        val userData = userFetcher.getUser(userId)
        assertEquals(userId, userData.id)
        assertEquals(userName, userData.name)
    }

    @Test
    fun getInvalidUser() {
        val e = assertThrows(Exception::class.java) { userFetcher.getUser(fakeUserId) }
        assertEquals("User does not exist", e.message)
    }

    @Test
    fun apiError() {
        val e = assertThrows(Exception::class.java) { userFetcher.getUser(errorUserId) }
        assertEquals("An error occured fetching user from the api", e.message)
    }

    @Test
    fun doesNotConnectOverHttpWhenSetToSecureOnly() {
        val e = assertThrows(Exception::class.java) { ApiUserFetcher(serverUrl, true) }
        assertEquals("Connection to API must be over https unless explicitly specified in the constructor", e.message)
    }
}