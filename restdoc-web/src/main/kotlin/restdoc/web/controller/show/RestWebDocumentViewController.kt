package restdoc.web.controller.show

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class RestWebDocumentViewController {

    @GetMapping("/show/{projectId}/restwebdocument/view")
    fun overview(@PathVariable projectId: String, model: Model): String {
        model.addAttribute("projectId", projectId)
        return "show/web-show-overview"
    }
}