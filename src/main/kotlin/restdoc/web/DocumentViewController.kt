package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/document/view")
@Controller
class DocumentViewController {

    @GetMapping("")
    fun list(): String {
        return ""
    }

    @GetMapping
    fun get(): String {
        return ""
    }
}