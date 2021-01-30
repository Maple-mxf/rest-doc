package smartdoc.dashboard.controller.console.view

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import restdoc.rpc.client.common.model.ApplicationType

@Controller
@RequestMapping("/devapp/view")
class DevApplicationApiViewController {

    @GetMapping("/{clientId}/api/list")
    fun list(model: Model,
             @PathVariable clientId: String,
             @RequestParam projectId: String,
             @RequestParam(required = false, defaultValue = "REST_WEB") ap: ApplicationType): String {

        model.addAttribute("clientId", clientId)
        model.addAttribute("ap", ap)
        model.addAttribute("projectId", projectId)

        return "client/api-list"
    }
}