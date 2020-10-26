package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.remoting.common.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.core.Status
import restdoc.web.core.schedule.ClientChannelManager
import restdoc.web.core.schedule.ClientAPIMemoryUnit

@Controller
@Verify
class MicroserviceViewController {

    @Autowired
    lateinit var clientChannelManager: ClientChannelManager

    @GetMapping("/microservice/view/index")
    fun index() = "microservice/index"

    @Deprecated(message = "list")
    @GetMapping("/microservice/view/{clientId}/list")
    fun list(@PathVariable clientId: String,
             @RequestParam ap: ApplicationType,
             model: Model): String {

        model.addAttribute("clientId", clientId)
        model.addAttribute("ap", ap)
        val client = clientChannelManager.findClient(clientId)
        if (client == null) Status.BAD_REQUEST.error("指定client不存在")
        model.addAttribute("service", client!!.service)

        return "microservice/dubbo-list"
    }

    @GetMapping("/microservice/view/list")
    fun listPage(@RequestParam ap: ApplicationType, model: Model): String {
        model.addAttribute("ap", ap)
        return "client/list"
    }
}