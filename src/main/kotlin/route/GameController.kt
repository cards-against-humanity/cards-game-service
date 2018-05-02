package route

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

}