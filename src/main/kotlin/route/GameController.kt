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
        return ResponseEntity.ok(gameManager.createGame(userId, gameData.gameName, gameData.maxPlayers, gameData.cardpackIds))
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

    @RequestMapping(value = "/games", method = [RequestMethod.GET])
    @ApiOperation(value = "Get a list of all running games")
    @ApiResponses(
            ApiResponse(code = 200, message = "Games retrieved")
    )
    fun getGameInfoList(): ResponseEntity<List<GameInfo>> {
        return try {
            ResponseEntity.ok(gameManager.getInfoList())
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @RequestMapping(value = "/{userId}/game", method = [RequestMethod.GET])
    @ApiOperation(value = "Get game info for a specific user")
    @ApiResponses(
            ApiResponse(code = 200, message = "Game info retrieved")
    )
    fun getGameForUser(@PathVariable userId: String): ResponseEntity<FOVGameData> {
        return try {
            ResponseEntity.ok(gameManager.getUserFOV(userId))
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @RequestMapping(value = "/{userId}/game/vote/{cardId}", method = [RequestMethod.PUT])
    @ApiOperation(value = "Cast vote as judge")
    @ApiResponses(
            ApiResponse(code = 204, message = "Vote succeeded"),
            ApiResponse(code = 403, message = "Invalid authorization")
    )
    fun vote(@PathVariable userId: String, @PathVariable cardId: String): ResponseEntity<Any> {
        return try {
            gameManager.vote(userId, cardId) // TODO - Handle other exception types
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @RequestMapping(value = "/{userId}/game/play/{cardId}", method = [RequestMethod.PUT])
    @ApiOperation(value = "Play a card")
    @ApiResponses(
            ApiResponse(code = 204, message = "Play succeeded"),
            ApiResponse(code = 403, message = "Invalid authorization")
    )
    fun playCard(@PathVariable userId: String, @PathVariable cardId: String): ResponseEntity<Any> {
        return try {
            gameManager.play(userId, cardId) // TODO - Handle other exception types
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    data class CreateGameData(
            @JsonProperty("gameName") val gameName: String,
            @JsonProperty("maxPlayers") val maxPlayers: Int,
            @JsonProperty("cardpackIds") val cardpackIds: List<String>
    )

}