package api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import model.BlackCard
import model.WhiteCard
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL
import java.util.*

class ApiCardFetcher(apiUrl: URL, isSecure: Boolean) : CardFetcher {

    constructor(apiUrl: URL) : this(apiUrl, true)

    private val apiPath = "${apiUrl.protocol}://${apiUrl.host}:${apiUrl.port}"
    private val client = OkHttpClient()

    init {
        if (isSecure && apiUrl.protocol != "https") {
            throw SecurityException("Connection to API must be over https unless explicitly specified in the constructor")
        }
    }

    override fun getCards(cardpackIds: List<String>): Pair<List<WhiteCard>, List<BlackCard>> {
        val whiteCards: MutableList<WhiteCard> = ArrayList()
        val blackCards: MutableList<BlackCard> = ArrayList()

        for (id in cardpackIds) {
            val response = client.newCall(Request.Builder().get().url("$apiPath/cardpack/$id").build()).execute()!!
            if (response.code() == 404) {
                throw Exception("Cardpack does not exist")
            } else if (response.code() != 200) {
                throw Exception("An error occurred fetching cards from the api")
            }
            val cardpack = ObjectMapper().readValue(response.body()!!.bytes(), ApiCardpack::class.java)
            whiteCards.addAll(cardpack.whiteCards)
            blackCards.addAll(cardpack.blackCards)
        }

        return Pair(whiteCards, blackCards)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class ApiCardpack(
            @JsonProperty("whiteCards") val whiteCards: List<ApiWhiteCard>,
            @JsonProperty("blackCards") val blackCards: List<ApiBlackCard>
    )

    private data class ApiWhiteCard(
            @JsonProperty("id") override val id: String,
            @JsonProperty("cardpackId") override val cardpackId: String,
            @JsonProperty("text") override val text: String
    ) : WhiteCard

    private data class ApiBlackCard(
            @JsonProperty("id") override val id: String,
            @JsonProperty("cardpackId") override val cardpackId: String,
            @JsonProperty("text") override val text: String,
            @JsonProperty("answerFields") override val answerFields: Int
    ) : BlackCard

}