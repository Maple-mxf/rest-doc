package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class DocumentNavViewController {

    @GetMapping("/{projectId}/document/nav/view")
    fun index(@PathVariable projectId: String, model: Model): String {
        model.addAttribute("projectId", projectId)
        return "explorer/nav"
    }

}