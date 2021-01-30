package smartdoc.dashboard.controller.console.view

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
@smartdoc.dashboard.base.auth.Verify
class ResourceViewController {

    @GetMapping("/{projectId}/resource/view/list")
    fun list(): String {
        return "resource/list"
    }

    @GetMapping("/{projectId}/resource/view/add")
    fun add(@PathVariable projectId: String): String {
        return "resource/add"
    }

    @GetMapping("/{projectId}/resource/view/table")
    fun tablePage(model: Model, @PathVariable projectId: String): String {
        model.addAttribute("projectId", projectId)
        return "explorer/table-content"
    }

}