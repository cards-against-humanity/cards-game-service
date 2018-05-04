package api

import model.User

interface UserFetcher {
    fun getUsers(cardpackIds: List<String>): List<User>

    fun getUser(cardpackId: String): User {
        return getUsers(listOf(cardpackId))[0]
    }
}