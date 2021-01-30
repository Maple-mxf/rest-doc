package smartdoc.dashboard.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import smartdoc.dashboard.controller.console.model.CreateProjectDto
import smartdoc.dashboard.controller.console.model.UpdateProjectDto
import smartdoc.dashboard.core.Result
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.model.Project
import smartdoc.dashboard.repository.ProjectRepository
import java.util.*

@RestController
@RequestMapping("/user/view")
@smartdoc.dashboard.base.auth.Verify
class UserViewController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var holderKit: smartdoc.dashboard.core.HolderKit

    @GetMapping("")
    fun list(): Result {
        val query = Query().addCriteria(Criteria("teamId").`is`(holderKit.user.teamId))
        query.with(by(desc("createTime")))
        return ok(projectRepository.list(query))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Result = ok(mongoTemplate.findById(id, Project::class.java))

    @PostMapping("")
    fun create(@RequestBody dto: CreateProjectDto): Result {

        val project = Project(id = smartdoc.dashboard.util.IDUtil.id(),
                name = dto.name,
                createTime = Date().time,
                desc = dto.desc)
        mongoTemplate.save(project)
        return ok()
    }

    @PatchMapping("")
    fun update(@RequestBody dto: UpdateProjectDto): Result {
        projectRepository.update(Project(
                id = dto.id,
                name = dto.name,
                createTime = null,
                desc = dto.desc))
        return ok()
    }
}