package restdoc.web.controller.console

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import restdoc.remoting.common.DubboExposedAPI
import restdoc.web.base.auth.Verify
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientRegistryCenter
import restdoc.web.model.ProjectType
import restdoc.web.repository.ProjectRepository
import restdoc.web.service.DubboDocumentService

@RestController
@Verify
class MicroserviceController {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var clientRegistryCenter: ClientRegistryCenter

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
                val apiList = this.clientRegistryCenter.getExposedAPIFilterService(service)
                if (apiList != null) {
                    // Convert Dubbo Exposed API to document
                    dubboDocumentService.sync(projectId = projectId, apiList = apiList as Collection<DubboExposedAPI>)
                }
            }
            else -> {
                throw RuntimeException("Not support")
            }
        }
        return ok()
    }


}