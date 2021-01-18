package restdoc.web.controller.console.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import restdoc.web.controller.console.model.DevApplicationVO
import restdoc.web.controller.console.model.layuiTableOK
import restdoc.web.repository.ProjectRepository
import restdoc.web.schedule.ClientManager

@RestController
@RequestMapping("/devapp")
class DevApplicationController(val clientManager: ClientManager, val projectRepository: ProjectRepository) {

    @GetMapping("/list")
    fun list(@RequestParam(required = false, defaultValue = "") projectId: String): Any {
        var ap = ""
        if (projectId.isNotBlank()) {
            val project = projectRepository.findById(projectId).orElseThrow { restdoc.web.core.Status.INVALID_REQUEST.instanceError() }
            ap = project.type.name
        }
        val adapters = clientManager.list()
        var adapterVOs = adapters
                .map {
                    DevApplicationVO(
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

        if (ap.isNotBlank()) adapterVOs = adapterVOs.filter { ap == it.applicationType }
        return layuiTableOK(adapterVOs, adapterVOs.size)
    }
}