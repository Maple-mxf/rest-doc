package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/docs")
class DocumentBuildController {

    @GetMapping("")
    fun index(model: Model): String {
        model.addAttribute("welcome", "welcome")
        return "index"
    }

    @GetMapping("/add_view")
    fun add(model: Model) = "add"
}