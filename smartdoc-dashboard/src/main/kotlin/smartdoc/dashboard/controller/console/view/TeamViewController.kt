package smartdoc.dashboard.controller.console.view

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/team/view")
@Controller
@smartdoc.dashboard.base.auth.Verify
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