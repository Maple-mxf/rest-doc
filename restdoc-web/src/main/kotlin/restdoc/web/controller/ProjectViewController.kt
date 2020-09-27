package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import restdoc.web.model.Project
import restdoc.web.model.ProjectType

@Controller
class ProjectViewController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @GetMapping("/project/view")
    fun list(@RequestParam type: ProjectType, model: Model): String {
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
}