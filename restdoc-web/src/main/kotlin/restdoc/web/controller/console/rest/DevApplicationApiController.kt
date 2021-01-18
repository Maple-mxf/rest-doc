package restdoc.web.controller.console.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.rpc.client.common.model.http.HttpApiDescriptor
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.DTreeResVO
import restdoc.web.controller.console.model.DTreeVO
import restdoc.web.controller.console.model.ImportApiDto
import restdoc.web.controller.console.model.NodeType
import restdoc.web.core.HolderKit
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.model.ProjectType
import restdoc.web.model.SYS_ADMIN
import restdoc.web.repository.HttpDocumentRepository
import restdoc.web.repository.ProjectRepository
import restdoc.web.repository.ResourceRepository
import restdoc.web.schedule.ApiManager
import restdoc.web.service.HttpDocumentService
import restdoc.web.util.MD5Util
import java.nio.charset.StandardCharsets


@RestController
@RequestMapping("/devapp")
@Verify(role = [SYS_ADMIN])
class DevApplicationApiController(val apiManager: ApiManager,
                                  val projectRepository: ProjectRepository,
                                  val httpDocumentService: HttpDocumentService,
                                  val holderKit: HolderKit,
                                  val httpDocumentRepository: HttpDocumentRepository,
                                  val resourceRepository: ResourceRepository
) {

    @RequestMapping(value = ["/{clientId}/api/list"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun list(@PathVariable clientId: String, @RequestParam at: ApplicationType): Any {

        val apiList = apiManager.list(clientId, at)

        val rootNode = DTreeVO(
                id = "root",
                title = "一级目录(虚拟)",
                parentId = "0",
                type = NodeType.RESOURCE,
                spread = true/*,
                iconClass = "dtree-icon-weibiaoti5"*/)

        val tree = when (at) {
            ApplicationType.REST_WEB -> {
                val descriptors = apiList as List<HttpApiDescriptor>

                val methods = descriptors
                        .map { t ->
                            DTreeVO(
                                    id = MD5Util.MD5Encode(t.pattern,StandardCharsets.UTF_8.name()),
                                    title = t.pattern,
                                    iconClass = "dtree-icon-normal-file",
                                    type = NodeType.API,
                                    parentId = MD5Util.MD5Encode(t.controller,StandardCharsets.UTF_8.name()))
                        }

                val pks = descriptors.groupBy { it.packageName }.keys.map {
                    DTreeVO(
                            id = MD5Util.MD5Encode(it,StandardCharsets.UTF_8.name()),
                            title = it,
                            iconClass = "dtree-icon-weibiaoti5",
                            parentId = "root")
                }

                val controllers = descriptors.groupBy { it.controller }.entries.map {
                    val arr = it.key.split(".")
                    DTreeVO(
                            id = MD5Util.MD5Encode(it.key,StandardCharsets.UTF_8.name()),
                            title = arr.last(),
                            iconClass = "dtree-icon-weibiaoti5",
                            parentId = MD5Util.MD5Encode(it.value.get(0).packageName,StandardCharsets.UTF_8.name()))
                }

                val res = mutableListOf<DTreeVO>()
                res.addAll(methods)
                res.addAll(pks)
                res.addAll(controllers)

                res
            }
            else -> throw NotImplementedError()
        }
        rootNode.children.add(
                DTreeVO(
                        id = "ID",
                        title = "it",
                        iconClass = "dtree-icon-weibiaoti5",
                        parentId = "root")
        )
        tree.add(rootNode)
        return DTreeResVO(data = tree)
    }

    // TODO  Distribute Lock
    @PostMapping("/api/import")
    fun importApi(@RequestBody dto: ImportApiDto): Any {

        val project = projectRepository.findById(dto.projectId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError("invalid projectId") }

        if (ProjectType.REST_WEB == project.type) {
            val table = httpDocumentService.transformToHttpApiDoc(dto.clientId, dto.projectId, holderKit.user.id)

            val savedPkResourceIds = mutableListOf<String>()
            val savedClassResourceIds = mutableListOf<String>()

            for (pkEntry in table) {
                val pkResource = pkEntry.key

                var haveDocs = false

                for (classEntry in pkEntry.value) {
                    val classResource = classEntry.key
                    val docs = classEntry.value
                    val matchedDocs = docs.filter { dto.apiIds.contains(it.id) }

                    if (matchedDocs.isNotEmpty()) {
                        haveDocs = true

                        if(!resourceRepository.existsById(classResource.id)) {
                            resourceRepository.save(classResource)
                            savedClassResourceIds.add(classResource.id!!)
                        }

                        for (document in matchedDocs) {

                            val docExist = httpDocumentRepository.existsById(document.id)

                            if (docExist){
                                val oldDocument = httpDocumentRepository.findById(document.id).orElse(null)

                                if (oldDocument == null) {
                                    httpDocumentRepository.save(document)
                                } else {
                                    // TODO
                                }
                            }
                            else {
                                httpDocumentRepository.save(document)
                            }
                        }
                    }
                }

                if (haveDocs){
                    if (!resourceRepository.existsById(pkResource.id))
                    {
                        resourceRepository.save(pkResource)
                        savedPkResourceIds.add(pkResource.id!!)
                    }
                }

            }
        }

        return ok()
    }
}