package restdoc.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Deprecated(message = "")
@Controller
class ServiceClientViewController {

    @GetMapping("/serviceClient/view/list")
    fun list(): String {
        return "cs/list"
    }


    @GetMapping("/serviceClient/view/index")
    fun index(): String {
        return "cs/index"
    }
}
