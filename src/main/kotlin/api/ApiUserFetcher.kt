package api

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import model.User
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

class ApiUserFetcher(apiUrl: URL, isSecure: Boolean) : UserFetcher {

    constructor(apiUrl: URL) : this(apiUrl, false)

    val apiPath = "${apiUrl.protocol}://${apiUrl.host}:${apiUrl.port}"
    val client = OkHttpClient()

    init {
        if (isSecure && apiUrl.protocol != "https") {
            println(apiUrl.protocol)
            throw SecurityException("Connection to API must be over https unless explicitly specified in the constructor")
        }
    }

    override fun getUsers(userIds: List<String>): List<User> {
        // TODO - Optimize by making only one network call
        val users: MutableList<User> = ArrayList()
        userIds.forEach { id ->
            val response = client.newCall(Request.Builder().get().url("$apiPath/user/$id").build()).execute()!!
            if (response.code() == 404) {
                throw Exception("User does not exist")
            } else if (response.code() != 200) {
                throw Exception("An error occured fetching user from the api")
            }
            val user = ObjectMapper().readValue(response.body()!!.bytes(), ApiUser::class.java)
            users.add(user)
        }
        return users
    }

    private data class ApiUser(
            @JsonProperty("id") override val id: String,
            @JsonProperty("name") override val name: String
    ) : User
}