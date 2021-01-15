package restdoc.web.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.core.Status

@Controller
@Verify
class MicroserviceViewController {

    @GetMapping("/microservice/view/index")
    fun index() = "microservice/index"


    @GetMapping("/microservice/view/list")
    fun listPage(@RequestParam ap: ApplicationType,
                 @RequestParam projectId: String, model: Model): String {
        model.addAttribute("ap", ap)
        model.addAttribute("projectId", projectId)
        return "client/list"
    }
}