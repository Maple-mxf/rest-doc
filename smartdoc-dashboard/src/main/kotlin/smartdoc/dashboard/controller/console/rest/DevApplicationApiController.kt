package smartdoc.dashboard.controller.console.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import restdoc.rpc.client.common.model.ApplicationType
import smartdoc.dashboard.controller.console.model.DTreeResVO
import smartdoc.dashboard.controller.console.model.DTreeVO
import smartdoc.dashboard.controller.console.model.ImportApiDto
import smartdoc.dashboard.controller.console.model.NodeType
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.model.ProjectType
import smartdoc.dashboard.model.SYS_ADMIN
import smartdoc.dashboard.repository.ProjectRepository
import smartdoc.dashboard.schedule.ApiManager
import smartdoc.dashboard.service.HttpDocumentService


@RestController
@RequestMapping("/devapp")
@smartdoc.dashboard.base.auth.Verify(role = [SYS_ADMIN])
class DevApplicationApiController(val apiManager: ApiManager,
                                  val projectRepository: ProjectRepository,
                                  val httpDocumentService: HttpDocumentService,
                                  val holderKit: smartdoc.dashboard.core.HolderKit
) {

    @RequestMapping(value = ["/{clientId}/api/list"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun list(@PathVariable clientId: String,
             @RequestParam at: ApplicationType,
             @RequestParam projectId: String): Any {

        val rootNode = DTreeVO(
                id = "root",
                title = "一级目录(虚拟)",
                parentId = "0",
                type = NodeType.RESOURCE,
                spread = true)

        val tree = when (at) {
            ApplicationType.REST_WEB -> {
                val res = mutableListOf<DTreeVO>()

                val docTableContent =
                        httpDocumentService.transformToHttpApiDoc(clientId, projectId, holderKit.user.id)

                for (entry in docTableContent) {
                    val pk = entry.key

                    entry.value.forEach {
                        val controller = it.key

                        val endpointDTreeVos = it.value.map {
                            DTreeVO(
                                    id = it.id!!,
                                    title = it.url,
                                    iconClass = "dtree-icon-normal-file",
                                    type = NodeType.API,
                                    parentId = controller.id!!)
                        }

                        res.addAll(endpointDTreeVos)

                        val classDTreeVo = DTreeVO(
                                id = controller.id!!,
                                title = controller.name!! ,
                                iconClass = "dtree-icon-weibiaoti5",
                                parentId = pk.id!!)

                        res.add(classDTreeVo)
                    }

                    val pkDTreeVo = DTreeVO(
                            id = pk.id!!,
                            title = pk.name!!,
                            iconClass = "dtree-icon-weibiaoti5",
                            parentId = "root")

                    res.add(pkDTreeVo)
                }
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

    @PostMapping("/api/import")
    fun importApi(@RequestBody dto: ImportApiDto): Any {

        val project = projectRepository.findById(dto.projectId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError("invalid projectId") }

        if (ProjectType.REST_WEB == project.type) {
            httpDocumentService.importApi(dto.clientId, dto.projectId, holderKit.user.id, dto.apiIds)
        }

        return ok()
    }
}