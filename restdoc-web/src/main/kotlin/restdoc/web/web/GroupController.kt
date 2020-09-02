package restdoc.web.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.base.auth.HolderKit
import restdoc.base.auth.Verify
import restdoc.core.Result
import restdoc.core.ok
import restdoc.model.Project
import restdoc.repository.GroupRepository
import restdoc.util.IDUtil
import restdoc.web.web.obj.CreateProjectDto
import restdoc.web.web.obj.UpdateProjectDto
import java.util.*

@RestController
@RequestMapping("/group")
@Verify
class GroupController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var groupRepository: GroupRepository

    @Autowired
    lateinit var holderKit: HolderKit

    @GetMapping("")
    fun list(): Result {
        val query = Query().addCriteria(Criteria("teamId").`is`(holderKit.user.teamId))
        query.with(by(desc("createTime")))
        return ok(groupRepository.list(query))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Result = ok(mongoTemplate.findById(id, Project::class.java))

    @PostMapping("")
    fun create(@RequestBody dto: CreateProjectDto): Result {

        val project = Project(id = IDUtil.id(),
                name = dto.name,
                createTime = Date().time,
                projectId = holderKit.user.teamId,
                desc = dto.desc)
        mongoTemplate.save(project)
        return ok()
    }

    @PatchMapping("")
    fun update(@RequestBody dto: UpdateProjectDto): Result {
        /*groupRepository.update(Gr(
                id = dto.id,
                name = dto.name,
                createTime = null,
                teamId = null,
                desc = dto.desc))*/
        return ok()
    }
}