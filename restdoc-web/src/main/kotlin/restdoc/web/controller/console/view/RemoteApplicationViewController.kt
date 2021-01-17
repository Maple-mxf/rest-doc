package restdoc.web.controller.console.view

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/remoteApplication/view")
class RemoteApplicationViewController {

    @GetMapping("/all")
    fun all():String = "client/client-all"
}