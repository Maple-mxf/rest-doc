package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.DubboExposedAPI
import restdoc.web.core.Status
import restdoc.web.core.schedule.ClientChannelManager
import restdoc.web.core.schedule.ClientExposedAPIManager

@Controller
class MicroserviceViewController {

    @Autowired
    lateinit var clientChannelManager: ClientChannelManager

    @Autowired
    lateinit var clientExposedAPIManager: ClientExposedAPIManager

    @GetMapping("/microservice/view/index")
    fun index() = "microservice/index"

    @GetMapping("/microservice/{service}/view/document/{id}")
    fun detail(@PathVariable service: String, @PathVariable id: String,
               @RequestParam ap: ApplicationType, model: Model): String {

        val apiList = clientExposedAPIManager.listBy(ap, service) as List<DubboExposedAPI>

        val methodDetail = apiList.flatMap { it.exposedMethods }
                .first { id == it.methodName + "->" + it.parameterClasses.joinToString("-") }

        model.addAttribute("methodDetail", methodDetail)

        return "microservice/dubbo-method-detail"
    }

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
}