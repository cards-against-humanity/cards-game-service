package api

import model.User

interface UserFetcher {
    fun getUsers(userIds: List<String>): List<User>

    fun getUser(userId: String): User {
        return getUsers(listOf(userId))[0]
    }
}