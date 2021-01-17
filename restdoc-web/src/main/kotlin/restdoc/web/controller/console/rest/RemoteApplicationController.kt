package restdoc.web.controller.console.rest

import com.google.common.collect.Ordering
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.web.controller.console.model.RemoteApplicationVO
import restdoc.web.schedule.ClientManager

@RestController
@RequestMapping("/remoteApplication")
class RemoteApplicationController(
        private val clientManager: ClientManager
) {

    @GetMapping("/all")
    fun all(): Any {
        val res = mutableMapOf<String, Any>()
        val adapters = clientManager.list()
        val adapterVOs = adapters.map {
            RemoteApplicationVO(
                    id = it.id(),
                    remoteAddress = "${it.host()}:${it.port()}",
                    hostname = it.hostName(),
                    os = it.os().name,
                    service = it.service(),
                    applicationType = it.applicationType().name,
                    state = it.state(),
                    connectTime = it.connectTime()
            )
        }



        res["code"] = 0
        res["count"] = 1
        res["msg"] = ""
        res["data"] = adapterVOs

        return res
    }
}