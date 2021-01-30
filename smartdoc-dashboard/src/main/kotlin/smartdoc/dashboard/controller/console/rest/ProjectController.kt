package smartdoc.dashboard.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import smartdoc.dashboard.controller.console.model.CreateProjectDto
import smartdoc.dashboard.controller.console.model.UpdateProjectDto
import smartdoc.dashboard.core.Result
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.model.ANY_ROLE
import smartdoc.dashboard.model.Project
import smartdoc.dashboard.model.ProjectType
import smartdoc.dashboard.model.SYS_ADMIN
import smartdoc.dashboard.repository.ProjectRepository
import smartdoc.dashboard.util.MD5Util
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/project")
class ProjectController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    /**
     * Add Search
     */
    @GetMapping("/list")
    @smartdoc.dashboard.base.auth.Verify(role = [SYS_ADMIN])
    fun list(@RequestParam(required = false, defaultValue = "0") page: Int,
             @RequestParam(required = false, defaultValue = "12") size: Int,
             @RequestParam type: ProjectType
    ): Result {
        return ok(projectRepository.page(Query().addCriteria(Criteria("type").`is`(type)), PageRequest.of(page, size)))
    }


    @GetMapping("/{id}")
    @smartdoc.dashboard.base.auth.Verify(role = [ANY_ROLE])
    fun get(@PathVariable id: String): Result = ok(mongoTemplate.findById(id, Project::class.java))

    @DeleteMapping("/{id}")
    @smartdoc.dashboard.base.auth.Verify(role = [SYS_ADMIN])
    fun delete(@PathVariable id: String): Result {
        val deleteResult = projectRepository.delete(Query().addCriteria(Criteria("_id").`is`(id)))
        return ok(deleteResult)
    }

    @PostMapping("")
    @smartdoc.dashboard.base.auth.Verify(role = [SYS_ADMIN])
    fun create(@RequestBody dto: CreateProjectDto): Result {
        if (dto.type == ProjectType.SPRINGCLOUD) Status.BAD_REQUEST.error("暂不支持SpringCloud项目")
        val projectId = smartdoc.dashboard.util.IDUtil.id()
        val project = Project(
                id = projectId,
                name = dto.name,
                createTime = Date().time,
                desc = dto.desc,
                type = dto.type,
                accessPassword =  MD5Util.encryptPassword("restdoc", projectId, 1024)
        )
        mongoTemplate.save(project)
        return ok()
    }

    @PatchMapping("")
    @smartdoc.dashboard.base.auth.Verify(role = [SYS_ADMIN])
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