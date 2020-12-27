package restdoc.web.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.schedule.ClientRegistryCenter

@Controller
@Verify
class ServiceClientViewController {

    @Autowired
    lateinit var clientRegistryCenter: ClientRegistryCenter

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

    @GetMapping("/serviceClient/{id}/view")
    fun detail(@PathVariable id: String, model: Model): String {
        val clientInfo =clientRegistryCenter.get(id)

        mapOf(
                "id" to clientInfo.id,
                "remoteAddress" to clientInfo.clientId,
                "hostname" to clientInfo.hostname,
                "osname" to clientInfo.osname,
                "service" to clientInfo.service,
                "applicationType" to clientInfo.applicationType,
                "serializationProtocol" to clientInfo.serializationProtocol
        ).forEach { (k, v) -> model.addAttribute(k, v) }

        return "overview/microservice-detail"
    }
}
