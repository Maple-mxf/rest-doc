package smartdoc.dashboard.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.model.Project
import smartdoc.dashboard.model.ProjectType
import smartdoc.dashboard.repository.ProjectRepository

@Controller
@smartdoc.dashboard.base.auth.Verify
class ProjectViewController {

    @Autowired
    @Deprecated(message = "mongoTemplate")
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @GetMapping("/project/view")
    fun list(@RequestParam(required = false, defaultValue = "REST_WEB") type: ProjectType, model: Model): String {
        model.addAttribute("projectType", type)
        return "project/list"
    }

    @GetMapping("/project/{id}/view")
    fun get(@PathVariable id: String, model: Model): String {

        val project = mongoTemplate.findById(id, Project::class.java)
        model.addAttribute("project", project)

        return "project/detail";
    }

    @GetMapping("/project/view/add")
    fun create(): String = "project/add"

    @GetMapping("/project/{id}/view/update")
    fun update(@PathVariable id: String, model: Model): String {
        val project = mongoTemplate.findById(id, Project::class.java)
        model.addAttribute("project", project)
        return "project/edit"
    }

    /**
     * apiNavigationPage
     */
    @GetMapping("/project/{id}/navigation/view")
    fun apiNavigationPage(@PathVariable id: String, model: Model): String {
        model.addAttribute("projectId", id)
        val project = projectRepository.findById(id).orElseThrow {  Status.INVALID_REQUEST.instanceError("id不存在") }
        return if (project.type == ProjectType.REST_WEB)
            "docs/web/api_navigation"
        else ""
    }

    @GetMapping("/project/{id}/settings/view")
    fun projectSettings(@PathVariable id: String, model: Model): String {
        val project = projectRepository.findById(id).orElseThrow {  Status.INVALID_REQUEST.instanceError("id不存在") }
        model.addAttribute("project", project)
        return if (project.type == ProjectType.REST_WEB) "project/settings/web_detail" else ""
    }
}