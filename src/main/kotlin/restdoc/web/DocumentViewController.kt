package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class DocumentViewController {

    @GetMapping("/{projectId}/document/view/list/")
    fun list(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/list"
    }

    @GetMapping("/{id}")
    fun get(): String {
        return ""
    }
}