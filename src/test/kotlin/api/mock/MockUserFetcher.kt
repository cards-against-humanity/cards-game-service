package api.mock

import api.UserFetcher
import model.User

class MockUserFetcher : UserFetcher {

    private val _users: MutableMap<String, User> = HashMap()

    val users: Map<String, User>
        get() = _users

    fun addUser(id: String, name: String) {
        _users[id] = MockUser(id, name)
    }

    fun removeUser(id: String) {
        _users.remove(id)
    }

    override fun getUsers(userIds: List<String>): List<User> {
        return userIds.map { id -> users[id]!! }
    }

    private data class MockUser(override val id: String, override val name: String) : User
}