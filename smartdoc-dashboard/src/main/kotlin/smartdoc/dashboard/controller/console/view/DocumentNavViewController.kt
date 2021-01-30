package smartdoc.dashboard.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.model.ProjectType
import smartdoc.dashboard.repository.ProjectRepository

@Controller
@smartdoc.dashboard.base.auth.Verify
@Deprecated(message = "DocumentNavViewController")
class DocumentNavViewController {

    @Autowired
    private lateinit var projectRepository: ProjectRepository


    @GetMapping("/{projectId}/document/nav/view")
    fun index(@PathVariable projectId: String, model: Model): String {

        model.addAttribute("projectId", projectId)

        val project = projectRepository.findById(projectId)
                .orElseThrow( Status.BAD_REQUEST::instanceError)

        model.addAttribute("projectName", project.name)

        return when {
            ProjectType.REST_WEB == project.type -> {
                model.addAttribute("hasInstance", false)
                "explorer/nav"
            }
            ProjectType.DUBBO.equals(project.type) -> {
                "overview/dubbo-overview"
            }
            else -> {
                ""
            }
        }
    }
}