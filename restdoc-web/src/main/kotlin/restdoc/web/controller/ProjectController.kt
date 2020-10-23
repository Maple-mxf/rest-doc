package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.web.base.auth.Verify
import restdoc.web.controller.obj.CreateProjectDto
import restdoc.web.controller.obj.UpdateProjectDto
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.model.Project
import restdoc.web.model.ProjectType
import restdoc.web.repository.ProjectRepository
import restdoc.web.util.IDUtil
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/project")
@Verify
class ProjectController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    /**
     * Add Search
     */
    @GetMapping("/list")
    fun list(@RequestParam(required = false, defaultValue = "0") page: Int,
             @RequestParam(required = false, defaultValue = "12") size: Int,
             @RequestParam type: ProjectType
    ): Result {
        return ok(projectRepository.page(Query().addCriteria(Criteria("type").`is`(type)), PageRequest.of(page, size)))
    }


    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Result = ok(mongoTemplate.findById(id, Project::class.java))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Result {
        val deleteResult = projectRepository.delete(Query().addCriteria(Criteria("_id").`is`(id)))
        return ok(deleteResult)
    }

    @PostMapping("")
    fun create(@RequestBody dto: CreateProjectDto): Result {
        if (dto.type == ProjectType.SPRINGCLOUD) Status.BAD_REQUEST.error("暂不支持SpringCloud项目")
        val project = Project(
                id = IDUtil.id(),
                name = dto.name,
                createTime = Date().time,
                desc = dto.desc,
                type = dto.type
        )
        mongoTemplate.save(project)
        return ok()
    }

    @PatchMapping("")
    fun update(@RequestBody @Valid dto: UpdateProjectDto): Result {
        projectRepository.update(Project(
                id = dto.id,
                name = dto.name,
                createTime = null,
                desc = dto.desc,
                type = dto.type
        ))
        return ok()
    }
}