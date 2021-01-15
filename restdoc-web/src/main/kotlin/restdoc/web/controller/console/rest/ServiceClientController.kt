package restdoc.web.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.SyncApiEmptyTemplateDto
import restdoc.web.core.HolderKit
import restdoc.web.core.ok
import restdoc.web.model.SYS_ADMIN
import restdoc.web.service.WebDocumentService


@RestController
class ServiceClientController {

    @Autowired
    lateinit var holderKit: HolderKit

    @Autowired
    lateinit var restWebDocumentService: WebDocumentService

    @Deprecated(message = "")
    @Verify
    @GetMapping("/serviceClient/list")
    fun list(@RequestParam(defaultValue = "DUBBO") type: ApplicationType): Any {
//        val clientKeys = this.clientRegistryCenter.getClientKeysFilterApplicationType(type)
        /*val services = this.clientRegistryCenter.getMulti(clientKeys)
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
                .toList()*/

        val res = mutableMapOf<String, Any>()
        res["code"] = 0
//        res["count"] = clientRegistryCenter.clientNum
        res["msg"] = ""
//        res["data"] = services

        return res
    }

    @Verify(role = [SYS_ADMIN])
    @PostMapping("/{projectId}/serviceClient/apiEmptyTemplate/sync")
    @Deprecated(message = "syncClientApiEmptyTemplate")
    fun syncClientApiEmptyTemplate(@RequestBody dto: SyncApiEmptyTemplateDto): Any {
        val resourceKeyDocumentMap =
                restWebDocumentService.syncHttpApiDoc(dto.remoteAddress, dto.projectId, holderKit.user.id)

//        resourceRepository.saveAll(resourceKeyDocumentMap.keys)
//        restWebDocumentRepository.saveAll(resourceKeyDocumentMap.values.flatten())

        return ok(dto.projectId)
    }
}
