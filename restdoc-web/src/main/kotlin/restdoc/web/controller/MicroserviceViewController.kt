package restdoc.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class MicroserviceViewController {

    @GetMapping("/microservice/view/index")
    fun index() = "microservice/index"
}