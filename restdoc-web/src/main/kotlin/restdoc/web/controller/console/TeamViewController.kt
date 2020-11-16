package restdoc.web.controller.console

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import restdoc.web.base.auth.Verify

@RequestMapping("/team/view")
@Controller
@Verify
class TeamViewController {

    @GetMapping("")
    fun list(): String {
        return ""
    }
    @GetMapping("/{id}")
    fun get(): String {
        return "";
    }
}