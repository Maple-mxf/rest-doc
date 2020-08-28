package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DocumentNavViewController {

    @GetMapping("/document/nav/view")
    fun index(): String {
        return "explorer/nav"
    }

}