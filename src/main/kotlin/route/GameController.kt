package route

import com.fasterxml.jackson.annotation.JsonProperty
import game.GameManager
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import model.FOVGameData
import model.GameInfo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GameController(private val gameManager: GameManager) {

    @RequestMapping(value = "/{userId}/game/create", method = [RequestMethod.POST])
    @ApiOperation(value = "Create game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Game successfully created")
    )
    fun createGame(@PathVariable userId: String, @RequestBody gameData: CreateGameData): ResponseEntity<FOVGameData> {
        return ResponseEntity.ok(gameManager.createGame(userId, gameData.gameName, gameData.maxPlayers, gameData.maxScore, gameData.cardpackIds))
    }

    @RequestMapping(value = "/{userId}/game/start", method = [RequestMethod.POST])
    @ApiOperation(value = "Start game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully started game"),
            ApiResponse(code = 400, message = "Game is already running or user is not in a game"),
            ApiResponse(code = 403, message = "User is not the owner")
    )
    fun startGame(@PathVariable userId: String): ResponseEntity<FOVGameData> {
        return ResponseEntity.ok(gameManager.startGame(userId))
    }

    @RequestMapping(value = "/{userId}/game/stop", method = [RequestMethod.POST])
    @ApiOperation(value = "Stop game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully stopped game"),
            ApiResponse(code = 400, message = "Game is not running or user is not in a game"),
            ApiResponse(code = 403, message = "User is not the owner")
    )
    fun stopGame(@PathVariable userId: String): ResponseEntity<FOVGameData> {
        return ResponseEntity.ok(gameManager.stopGame(userId))
    }

    @RequestMapping(value = "/{userId}/game/{gameName}/join", method = [RequestMethod.POST])
    @ApiOperation(value = "Join game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully joined game")
    )
    fun joinGame(@PathVariable userId: String, @PathVariable gameName: String): ResponseEntity<FOVGameData> {
        return ResponseEntity.ok(gameManager.joinGame(userId, gameName))
    }

    @RequestMapping(value = "/{userId}/game", method = [RequestMethod.DELETE])
    @ApiOperation(value = "Leave game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully left game")
    )
    fun leaveGame(@PathVariable userId: String): ResponseEntity<Any> {
        gameManager.leaveGame(userId)
        return ResponseEntity.noContent().build()
    }

    @RequestMapping(value = "/{kickerId}/game/players/{kickeeId}", method = [RequestMethod.DELETE])
    @ApiOperation(value = "Kick user")
    @ApiResponses(
            ApiResponse(code = 200, message = "Successfully kicked user"),
            ApiResponse(code = 403, message = "Kicker is not the owner")
    )
    fun kickUser(@PathVariable kickerId: String, @PathVariable kickeeId: String): ResponseEntity<FOVGameData> {
        return ResponseEntity.ok(gameManager.kick(kickerId, kickeeId))
    }

    @RequestMapping(value = "/{userId}/game/play", method = [RequestMethod.PUT])
    @ApiOperation(value = "Play a card")
    @ApiResponses(
            ApiResponse(code = 204, message = "Play succeeded"),
            ApiResponse(code = 403, message = "Invalid authorization")
    )
    fun playCard(@PathVariable userId: String, @RequestBody cardIds: List<String>): ResponseEntity<Any> {
        gameManager.play(userId, cardIds)
        return ResponseEntity.noContent().build()
    }

    @RequestMapping(value = "/{userId}/game/vote/{cardId}", method = [RequestMethod.PUT])
    @ApiOperation(value = "Cast vote as judge")
    @ApiResponses(
            ApiResponse(code = 204, message = "Vote succeeded"),
            ApiResponse(code = 403, message = "User is not the judge")
    )
    fun vote(@PathVariable userId: String, @PathVariable cardId: String): ResponseEntity<Any> {
        gameManager.vote(userId, cardId)
        return ResponseEntity.noContent().build()
    }

    @RequestMapping(value = "/{userId}/game/continue", method = [RequestMethod.PUT])
    @ApiOperation(value = "Start next round")
    @ApiResponses(
            ApiResponse(code = 204, message = "Next round started")
    )
    fun startNextRound(@PathVariable userId: String): ResponseEntity<Any> {
        gameManager.startNextRound(userId)
        return ResponseEntity.noContent().build()
    }

    @RequestMapping(value = "/{userId}/game/messages", method = [RequestMethod.PUT])
    @ApiOperation(value = "Post message to game")
    @ApiResponses(
            ApiResponse(code = 200, message = "Message successfully posted")
    )
    fun sendMessage(@PathVariable userId: String, @RequestBody message: String): ResponseEntity<FOVGameData> {
        gameManager.sendMessage(userId, message)
        return ResponseEntity.ok(gameManager.getUserFOV(userId))
    }

    @RequestMapping(value = "/{userId}/game", method = [RequestMethod.GET])
    @ApiOperation(value = "Get game info for a specific user")
    @ApiResponses(
            ApiResponse(code = 200, message = "Game info retrieved")
    )
    fun getGameForUser(@PathVariable userId: String): ResponseEntity<FOVGameData> {
        return ResponseEntity.ok(gameManager.getUserFOV(userId))
    }

    @RequestMapping(value = "/games", method = [RequestMethod.GET])
    @ApiOperation(value = "Get a list of all running games")
    @ApiResponses(
            ApiResponse(code = 200, message = "Games retrieved")
    )
    fun getGameInfoList(): ResponseEntity<List<GameInfo>> {
        return ResponseEntity.ok(gameManager.getInfoList())
    }

    data class CreateGameData(
            @JsonProperty("gameName") val gameName: String,
            @JsonProperty("maxPlayers") val maxPlayers: Int,
            @JsonProperty("maxScore") val maxScore: Int,
            @JsonProperty("cardpackIds") val cardpackIds: List<String>
    )

}