package api

import com.google.common.cache.CacheBuilder
import model.User
import java.time.Duration

class CachedUserFetcher(private val fetcher: UserFetcher, maximumSize: Long, itemTimeout: Duration): UserFetcher {

    private val cache = CacheBuilder.newBuilder().maximumSize(maximumSize).expireAfterWrite(itemTimeout).build<String, User>()

    override fun getUsers(userIds: List<String>): List<User> {
        val users: MutableMap<String, User> = HashMap()
        val missingUserIds: MutableList<String> = ArrayList()
        for (id in userIds) {
            val user = cache.getIfPresent(id)
            if (user == null) {
                missingUserIds.add(id)
            } else {
                users[id] = user
            }
        }
        val missingUsers = fetcher.getUsers(missingUserIds)
        missingUsers.forEach { user ->
            users[user.id] = user
            cache.put(user.id, user)
        }
        return userIds.map { id -> users[id]!! }
    }

}