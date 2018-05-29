package game.messaging

import kotlin.collections.ArrayList

class GameMessageModule(private val maxSize: Int) {
    private val messageModels: MutableList<MessageModel> = ArrayList()

    init {
        if (maxSize < 0) {
            throw IllegalArgumentException("maxSize cannot be a negative number")
        }
    }

    fun addMessage(messageModel: MessageModel) {
        messageModels.add(messageModel)
        if (messageModels.size > maxSize) {
            messageModels.removeAt(0)
        }
    }

    fun getRecentMessages(count: Int): List<MessageModel> {
        return messageModels.takeLast(count)
    }
}