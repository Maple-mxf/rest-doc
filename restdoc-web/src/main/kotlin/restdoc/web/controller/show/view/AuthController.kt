package restdoc.web.controller.show.view

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController(value = "showAuthController")
@RequestMapping("/or")
class AuthController {

    @GetMapping("/{projectId}")
    fun auth(@PathVariable projectId: String): String {
        return ""
    }

}