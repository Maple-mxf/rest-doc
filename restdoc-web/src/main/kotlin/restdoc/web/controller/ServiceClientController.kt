package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.web.core.schedule.ClientManager

@RestController
class ServiceClientController {

    @Autowired
    lateinit var clientManager: ClientManager

    @GetMapping("/serviceClient/list")
    fun list(): Any {

        val services = clientManager.clients.map {
            mapOf(
                    "remoteAddress" to it.key,
                    "hostname" to it.value.hostname,
                    "osname" to it.value.osname
            )
        }
                .toList()

        val res = mutableMapOf<String, Any>()
        res["code"] = 0
        res["count"] = clientManager.clients.size
        res["msg"] = ""
        res["data"] = services

        return res
    }
}
