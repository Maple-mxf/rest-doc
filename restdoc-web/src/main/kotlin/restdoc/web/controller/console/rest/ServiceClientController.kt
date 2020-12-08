package restdoc.web.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.SyncApiEmptyTemplateDto
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientRegistryCenter
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.repository.ResourceRepository
import restdoc.web.repository.RestWebDocumentRepository
import restdoc.web.service.RestWebDocumentService


@RestController

class ServiceClientController {

    @Autowired
    lateinit var clientRegistryCenter: ClientRegistryCenter

    @Autowired
    lateinit var scheduleController: ScheduleController

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @Autowired
    lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @Autowired
    lateinit var restWebDocumentService: RestWebDocumentService

    @Deprecated(message = "")
    @Verify
    @GetMapping("/serviceClient/list")
    fun list(@RequestParam(defaultValue = "DUBBO") type: ApplicationType): Any {
        val clientKeys = this.clientRegistryCenter.getClientKeysFilterApplicationType(type)
        val services = this.clientRegistryCenter.getMulti(clientKeys)
                .map {
                    mapOf(
                            "id" to it.id,
                            "remoteAddress" to it.clientId,
                            "hostname" to it.hostname,
                            "osname" to it.osname,
                            "service" to it.service,
                            "applicationType" to it.applicationType,
                            "serializationProtocol" to it.serializationProtocol
                    )
                }
                .toList()

        val res = mutableMapOf<String, Any>()
        res["code"] = 0
        res["count"] = clientRegistryCenter.clientNum
        res["msg"] = ""
        res["data"] = services

        return res
    }

    @Verify(role = ["SYS_ADMIN"])
    @PostMapping("/{projectId}/serviceClient/apiEmptyTemplate/sync")
    fun syncClientApiEmptyTemplate(@RequestBody dto: SyncApiEmptyTemplateDto): Any {

        val resourceKeyDocumentMap = restWebDocumentService.syncHttpApiDoc(dto.remoteAddress, dto.projectId!!)

        resourceRepository.saveAll(resourceKeyDocumentMap.keys)
        restWebDocumentRepository.saveAll(resourceKeyDocumentMap.values.flatten())

        return ok(dto.projectId)
    }
}
