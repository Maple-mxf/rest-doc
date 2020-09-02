package restdoc.web.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/team/view")
@Controller
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