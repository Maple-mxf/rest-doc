package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import restdoc.web.base.auth.Verify
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientExposedAPIManager
import restdoc.web.model.ProjectType
import restdoc.web.repository.ProjectRepository
import restdoc.web.service.DubboDocumentService

@RestController
@Verify
class MicroserviceController {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var exposedAPIManager: ClientExposedAPIManager

    @Autowired
    lateinit var dubboDocumentService: DubboDocumentService

    @PostMapping("/{projectId}/microservice/api/sync")
    fun syncAPI(@PathVariable projectId: String,
                @RequestParam service: String/*,
                @RequestParam id: String*/
    ): Result {
        val (_, _, _, _, _, type) =
                projectRepository.findById(projectId).orElseThrow { Status.BAD_REQUEST.instanceError("projectId错误") }
        when (type) {
            ProjectType.DUBBO -> {
                val apiContext = exposedAPIManager.dubboExposedExposedAPI.keys.first { it.service == service }
                val apiList = exposedAPIManager.dubboExposedExposedAPI[apiContext]
                if (apiList != null) {
                    // Convert Dubbo Exposed API to document
                    dubboDocumentService.sync(projectId = projectId, apiList = apiList)
                }
            }
            else -> {
                throw RuntimeException("Not support")
            }
        }
        return ok()
    }


    /*@GetMapping("/microservice/{id}/exposedapi")
    fun getExposedAPI(@PathVariable id: String,
                      @RequestParam ap: String): Any {

        val applicationType = ApplicationType.valueOf(ap.toUpperCase())

        val apiList = clientExposedAPIManager.listBy(applicationType, id)

        return when (applicationType) {
            ApplicationType.DUBBO -> {
                val dubboAPIList: List<DubboExposedAPI> = apiList as List<DubboExposedAPI>
                val navTree = dubboAPIList.map {
                    val children = it.exposedMethods
                            .map { method ->
                                NavNode(
                                        id = method.methodName + "->" + method.parameterClasses.joinToString(separator = "-"),
                                        title = method.methodName,
                                        field = null,
                                        children = mutableListOf(),
                                        pid = it.name,
                                        type = NodeType.API
                                )
                            }.toMutableList()
                    val node = NavNode(
                            id = it.name,
                            title = it.name,
                            field = null,
                            children = children,
                            pid = ROOT_NAV.id)

                    node.children = children
                    node
                }
                ok(navTree)
            }
            else -> {
                throw Throwable("參數錯誤")
            }
        }
    }*/
}