package restdoc.web.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.web.base.auth.Verify
import restdoc.web.model.Project
import restdoc.web.model.ProjectType

@Controller
@Verify
class ProjectViewController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

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
        val project = mongoTemplate.findById(id, Project::class.java)
        return if (project.type == ProjectType.REST_WEB)
            "docs/web/api_navigation"
        else ""
    }
}