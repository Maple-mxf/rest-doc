package smartdoc.dashboard.controller.console.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import smartdoc.dashboard.controller.console.model.DevApplicationVO
import smartdoc.dashboard.controller.console.model.layuiTableOK
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.repository.ProjectRepository
import smartdoc.dashboard.schedule.ClientManager

@RestController
@RequestMapping("/devapp")
class DevApplicationController(val clientManager: ClientManager, val projectRepository: ProjectRepository) {

    @GetMapping("/list")
    fun list(@RequestParam(required = false, defaultValue = "") projectId: String): Any {
        var ap = ""
        if (projectId.isNotBlank()) {
            val project = projectRepository.findById(projectId).orElseThrow {  Status.INVALID_REQUEST.instanceError() }
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