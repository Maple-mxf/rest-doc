package smartdoc.dashboard.controller.console.view

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/devapp/view")
class DevApplicationViewController {

    @GetMapping("/all")
    fun all(): String = "client/client-all"

    @GetMapping("/list")
    fun list(@RequestParam projectId: String, model: Model): String  {
        model.addAttribute("projectId", projectId)
        return "client/list"
    }
}