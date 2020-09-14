package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import restdoc.remoting.common.ApplicationType
import restdoc.web.controller.obj.SyncApiEmptyTemplateDto
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientChannelManager
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.Document
import restdoc.web.model.Project
import restdoc.web.model.Resource
import restdoc.web.model.URIVarDescriptor
import restdoc.web.repository.DocumentRepository
import restdoc.web.repository.ProjectRepository
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

    @Autowired
    lateinit var documentRepository: DocumentRepository

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @GetMapping("/serviceClient/list")
    fun list(@RequestParam type: ApplicationType): Any {

        val services = clientChannelManager.clients
                .filter { it.value.applicationType == type }
                .map {
                    mapOf(
                            "id" to it.value.id,
                            "remoteAddress" to it.value.clientId,
                            "hostname" to it.value.hostname,
                            "osname" to it.value.osname,
                            "service" to it.value.service,
                            "applicationType" to it.value.applicationType,
                            "serializationProtocol" to it.value.serializationProtocol
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

    @PostMapping("/serviceClient/apiEmptyTemplate/sync")
    fun syncClientApiEmptyTemplateToExistProject(
            @RequestBody dto: SyncApiEmptyTemplateDto
    ): Any {

        val project = Project(
                id = IDUtil.id(),
                name = dto.name!!,
                desc = dto.name,
                createTime = now(),
                accessPwd = null)

        val resourceKeyDocumentMap = syncApiEmptyTemplates(dto.clientId, project.id)

        resourceRepository.saveAll(resourceKeyDocumentMap.keys)
        documentRepository.saveAll(resourceKeyDocumentMap.values.flatten())

        projectRepository.save(project)

        return ok(project.id)
    }

    @PostMapping("/{projectId}/serviceClient/apiEmptyTemplate/sync")
    fun syncClientApiEmptyTemplate(@RequestBody dto: SyncApiEmptyTemplateDto): Any {

        val resourceKeyDocumentMap = syncApiEmptyTemplates(dto.clientId, dto.projectId!!)

        resourceRepository.saveAll(resourceKeyDocumentMap.keys)
        documentRepository.saveAll(resourceKeyDocumentMap.values.flatten())

        return ok(dto.projectId)
    }

    private fun syncApiEmptyTemplates(clientId: String, projectId: String): Map<Resource, List<Document>> {

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
                            URIVarDescriptor(uriField, null, null)
                        }

                        Document(id = IDUtil.id(),
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
