package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/project/view")
@Controller
class TeamViewController {

    @GetMapping("")
    fun list(): String {
        return ""
    }

    fun get(): String {
        return "";
    }
}