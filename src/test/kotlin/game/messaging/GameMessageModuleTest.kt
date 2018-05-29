package game.messaging

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameMessageModuleTest {
    private val moduleSize = 10

    private var messageModule = GameMessageModule(0)

    @BeforeEach
    fun reset() {
        messageModule = GameMessageModule(moduleSize)
    }

    @Test
    fun canAddMessagesWithoutError() {
        messageModule.addMessage(MessageModel("1", "hello"))
    }

    @Test
    fun canAddMessagesBeyondModuleSize() {
        for (i in 1..(moduleSize * 2)) {
            messageModule.addMessage(MessageModel("1", "hello"))
        }
    }

    @Test
    fun doesNotStoreMoreMessagesThanAllowed() {
        for (i in 1..(moduleSize * 2)) {
            messageModule.addMessage(MessageModel("1", "hello"))
        }
        assertEquals(moduleSize, messageModule.getRecentMessages(moduleSize * 2).size)
    }

    @Test
    fun retrievesAllMessagesIfAmountAskedForIsGreaterThanTotal() {
        assertEquals(0, messageModule.getRecentMessages(moduleSize).size)
    }

    @Test
    fun containsMostRecentMessageFirst() {
        for (i in 1..(moduleSize * 2)) {
            messageModule.addMessage(MessageModel("1", "hello"))
        }
        messageModule.addMessage(MessageModel("1", "new message"))
        assertEquals("new message", messageModule.getRecentMessages(1)[0].text)
    }

    @Test
    fun cannotInstantiateWithNegativeSize() {
        val e = assertThrows(IllegalArgumentException::class.java) { GameMessageModule(-1) }
        assertEquals(e.message, "maxSize cannot be a negative number")
    }
}