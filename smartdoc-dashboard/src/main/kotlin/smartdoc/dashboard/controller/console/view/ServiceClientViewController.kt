package smartdoc.dashboard.controller.console.view

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.rpc.client.common.model.ApplicationType

@Controller
@smartdoc.dashboard.base.auth.Verify
class ServiceClientViewController {


    @GetMapping("/serviceClient/view/list")
    fun list() = "cs/list"

    @GetMapping("/serviceClient/view/index")
    fun index() = "cs/index"

    @GetMapping("/serviceClient/{clientId}/apiList/view")
    fun apiList(model: Model,
                @PathVariable clientId: String,
                @RequestParam(required = false,defaultValue = "REST_WEB") ap: ApplicationType): String {
        model.addAttribute("clientId", clientId)
        model.addAttribute("ap", ap)
        return "client/api-list"
    }


}
