package restdoc.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
open class AdminLoginController {

    @GetMapping("")
    fun loginView() = "auth/admin_login"

}