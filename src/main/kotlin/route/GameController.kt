package route

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GameController() {

    @RequestMapping(value = "/ping", method = [RequestMethod.GET])
    @ApiOperation(value = "Get a user")
    @ApiResponses(
            ApiResponse(code = 200, message = "User retrieved"),
            ApiResponse(code = 404, message = "User does not exist")
    )
    fun ping(): ResponseEntity<Any> {
        return ResponseEntity.ok("success")
    }

}
