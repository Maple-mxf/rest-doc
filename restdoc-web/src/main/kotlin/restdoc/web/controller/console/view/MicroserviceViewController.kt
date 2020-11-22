package restdoc.web.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.remoting.common.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.core.Status
import restdoc.web.core.schedule.ClientRegistryCenter

@Controller
@Verify
class MicroserviceViewController {

    @Autowired
    lateinit var clientRegistryCenter: ClientRegistryCenter

    @GetMapping("/microservice/view/index")
    fun index() = "microservice/index"

    @Deprecated(message = "list")
    @GetMapping("/microservice/view/{clientId}/list")
    fun list(@PathVariable clientId: String,
             @RequestParam ap: ApplicationType,
             model: Model): String {

        model.addAttribute("clientId", clientId)
        model.addAttribute("ap", ap)
        val client = clientRegistryCenter.get(clientId)
        if (client == null) Status.BAD_REQUEST.error("指定client不存在")
        model.addAttribute("service", client!!.service)

        return "microservice/dubbo-list"
    }

    @GetMapping("/microservice/view/list")
    fun listPage(@RequestParam ap: ApplicationType,
                 @RequestParam projectId: String, model: Model): String {
        model.addAttribute("ap", ap)
        model.addAttribute("projectId", projectId)
        return "client/list"
    }
}