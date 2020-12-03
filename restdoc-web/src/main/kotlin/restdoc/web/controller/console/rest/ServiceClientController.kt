package restdoc.web.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import restdoc.remoting.common.ApplicationType
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.*
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientRegistryCenter
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.*
import restdoc.web.repository.ResourceRepository
import restdoc.web.repository.RestWebDocumentRepository
import restdoc.web.util.IDUtil
import restdoc.web.util.IDUtil.now


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

        val resourceKeyDocumentMap = syncApiEmptyTemplates(dto.remoteAddress, dto.projectId!!)

        resourceRepository.saveAll(resourceKeyDocumentMap.keys)
        restWebDocumentRepository.saveAll(resourceKeyDocumentMap.values.flatten())

        return ok(dto.projectId)
    }

    private fun syncApiEmptyTemplates(clientId: String, projectId: String): Map<Resource, List<RestWebDocument>> {

        // Invoke remote client api info
        val emptyApiTemplates = scheduleController.syncGetEmptyApiTemplates(clientId)

        val pid = "root"

        return emptyApiTemplates
                // Group by resource
                .groupBy { it.controller }
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
                    val documents = it.value.map { template ->

                        val uriVarDescriptors = template.uriVarFields.map { uriField ->
                            URIVarDescriptor(uriField, "", null)
                        }

                        RestWebDocument(id = IDUtil.id(),
                                projectId = projectId,
                                name = template.function,
                                resource = resource.id!!,
                                url = template.pattern,
                                description = String.format("%s:%s", template.controller, template.function),
                                requestHeaderDescriptor = mutableListOf(),
                                requestBodyDescriptor = mutableListOf(),
                                responseBodyDescriptors = mutableListOf(),
                                method = method(template.supportMethod.map { HttpMethod.valueOf(it) }),
                                uriVarDescriptors = uriVarDescriptors
                        )
                    }
                    resource to documents
                }
                .toMap()
    }

    private fun method(methods: Collection<HttpMethod>): HttpMethod {
        if (methods.isEmpty()) return HttpMethod.GET
        if (methods.size == 1) return methods.toTypedArray()[0]
        return if (methods.contains(HttpMethod.GET)) HttpMethod.GET else HttpMethod.POST
    }


}
