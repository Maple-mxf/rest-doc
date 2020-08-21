package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/group/view")
@Controller
class GroupViewController {

    @GetMapping("")
    fun list(): String {
        return ""
    }

    @GetMapping("/{id}")    fun get(): String {
        return ""
    }
}