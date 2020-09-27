package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import restdoc.web.core.schedule.ClientChannelManager

@Deprecated(message = "ServiceClientViewController")
@Controller
class ServiceClientViewController {

    @Autowired
    lateinit var clientChannelManager: ClientChannelManager

    @GetMapping("/serviceClient/view/list")
    fun list(): String {
        return "cs/list"
    }


    @GetMapping("/serviceClient/view/index")
    fun index(): String {
        return "cs/index"
    }

    @GetMapping("/serviceClient/{id}/view")
    fun detail(@PathVariable id: String, model: Model): String {
        val clientInfo = clientChannelManager.clients
                .values.first { it.id == id }

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
