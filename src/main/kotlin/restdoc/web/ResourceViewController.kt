package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ResourceViewController {

    @GetMapping("/{projectId}/resource/view/list")
    fun list(): String {
        return "resource/list"
    }
}