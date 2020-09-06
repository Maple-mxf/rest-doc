package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientChannelManager
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.Document
import restdoc.web.model.Resource
import restdoc.web.repository.ResourceRepository
import restdoc.web.util.IDUtil
import restdoc.web.util.IDUtil.now

@RestController
class ServiceClientController {

    @Autowired
    lateinit var clientChannelManager: ClientChannelManager

    @Autowired
    lateinit var scheduleController: ScheduleController

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @GetMapping("/serviceClient/list")
    fun list(): Any {

        val services = clientChannelManager.clients.map {
            mapOf(
                    "remoteAddress" to it.key,
                    "hostname" to it.value.hostname,
                    "osname" to it.value.osname,
                    "service" to it.value.service
            )
        }
                .toList()

        val res = mutableMapOf<String, Any>()
        res["code"] = 0
        res["count"] = clientChannelManager.clients.size
        res["msg"] = ""
        res["data"] = services

        return res
    }

    @GetMapping("/{projectId}/serviceClient/{id}/syncApiEmptyTemplate")
    fun syncClientApiEmptyTemplate(@PathVariable id: String, @PathVariable projectId: String): Any {

        // Invoke remote client api info
        val emptyApiTemplates = scheduleController.syncGetEmptyApiTemplates(id)

        val pid = "root"

        // Map to resources


        // Group by resource
        emptyApiTemplates.groupBy { it.controller }
                .map {

                    // Map key to resource
                    val resource = Resource(
                            id = IDUtil.id(),
                            tag = it.key,
                            name = it.key,
                            pid = pid,
                            projectId = projectId,
                            createTime = now(),
                            createBy = "Default"
                    )

                    // Map value to document
                    it.value.map {
                        Document(id = IDUtil.id(),
                                projectId = projectId,
                                name = it.function,
                                resource = resource.id!!,
                                url = it.pattern,
                                description = String.format("%s:%s", it.controller, it.function),
                                requestHeaderDescriptor = mutableListOf(),
                                requestBodyDescriptor = mutableListOf(),
                                responseBodyDescriptors = mutableListOf(),
                                method = HttpMethod.valueOf(it.supportMethod[0]),
                                uriVariables =null
                        )
                    }
                }




        return ok()
    }
}
