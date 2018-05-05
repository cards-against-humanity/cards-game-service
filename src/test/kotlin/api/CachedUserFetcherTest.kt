package api

import api.mock.MockUserFetcher
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals

class CachedUserFetcherTest {

    private val cacheSize = 100L
    private val cacheTimeout = Duration.ofMillis(500)
    private var mockFetcher = MockUserFetcher()
    private var cachedFetcher = CachedUserFetcher(mockFetcher, cacheSize, cacheTimeout)

    private val userId = "1234"
    private val userName = "Tommy"

    private val userIdTwo = "4321"
    private val userNameTwo = "Jeremy"

    @BeforeEach
    fun reset() {
        mockFetcher = MockUserFetcher()
        cachedFetcher = CachedUserFetcher(mockFetcher, cacheSize, cacheTimeout)
        mockFetcher.addUser(userId, userName)
        mockFetcher.addUser(userIdTwo, userNameTwo)
    }

    @Test
    fun retrievesUserFromCacheAfterSecondRequest() {
        cachedFetcher.getUser(userId)
        mockFetcher.removeUser(userId)
        cachedFetcher.getUser(userId)
    }

    @Test
    fun removedTimedOutElements() {
        cachedFetcher.getUser(userId)
        mockFetcher.removeUser(userId)
        Thread.sleep(cacheTimeout.toMillis())
        assertThrows(NullPointerException::class.java) { cachedFetcher.getUser(userId) }
    }

    @Test
    fun retrievesSomeCachedElementsAndSomeNotCachedElements() {
        cachedFetcher.getUser(userId)
        mockFetcher.removeUser(userId)
        val users = cachedFetcher.getUsers(listOf(userId, userIdTwo))

        assertEquals(userId, users[0].id)
        assertEquals(userIdTwo, users[1].id)
    }
}