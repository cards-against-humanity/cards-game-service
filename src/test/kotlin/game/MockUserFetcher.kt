package game

import api.UserFetcher
import model.User

class MockUserFetcher : UserFetcher {
    private val users: MutableMap<String, User> = HashMap()

    fun setUser(id: String, name: String) {
        users[id] = MockUser(id, name)
    }

    override fun getUsers(userIds: List<String>): List<User> {
        return userIds.map { id -> users[id]!! }
    }

    private data class MockUser(override val id: String, override val name: String) : User
}