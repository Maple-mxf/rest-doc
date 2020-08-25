package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import restdoc.base.auth.HolderKit
import restdoc.base.auth.Verify
import restdoc.core.Result
import restdoc.core.executor.ExecutorDelegate
import restdoc.core.ok
import restdoc.model.BodyFieldDescriptor
import restdoc.model.FieldType
import restdoc.model.HeaderFieldDescriptor
import restdoc.model.Project
import restdoc.repository.ProjectRepository
import restdoc.util.IDUtil
import restdoc.util.JsonDeProjector
import restdoc.web.obj.CreateProjectDto
import restdoc.web.obj.RequestVo
import restdoc.web.obj.UpdateProjectDto
import java.util.*

/**
 * @see Project
 */
@RestController
@RequestMapping("/document")
@Verify
class DocumentController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var holderKit: HolderKit

    @Autowired
    lateinit var delete: ExecutorDelegate

    @GetMapping("/list/{projectId}")
    fun list(@PathVariable projectId: String): Result {
        val query = Query().addCriteria(Criteria("projectId").`is`(projectId))
        query.with(by(desc("createTime")))
        return ok(projectRepository.list(query))
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
        projectRepository.update(Project(
                id = dto.id,
                name = dto.name,
                createTime = null,
                projectId = null,
                desc = dto.desc))
        return ok()
    }

    @PostMapping("/deProject")
    @ResponseBody
    fun deProjector(@RequestBody tree: JsonNode): Result = ok(JsonDeProjector(tree).deProject())


    @PostMapping("/project")
    @ResponseBody
    fun projector(@RequestBody requestVo: RequestVo): Result {
        return ok()
    }

    @PostMapping("/execute")
    @ResponseBody
    fun execute(@RequestBody requestVo: RequestVo): Result {

        val requestHeaderDescriptor = requestVo.headers.map {
            HeaderFieldDescriptor(
                    field = it.headerKey,
                    value = it.headerValue.split(","),
                    description = it.headerDescription,
                    optional = it.headerConstraint
            )
        }

        val requestBodyDescriptor = requestVo.requestBody.map {
            BodyFieldDescriptor(
                    path = it.requestFieldPath,
                    value = it.requestFieldValue,
                    description = it.requestFieldDescription,
                    type = FieldType.valueOf(it.requestFieldType),
                    optional = it.requestFieldConstraint,
                    defaultValue = null
            )
        }

        val executeResult = delete.execute(
                url = requestVo.url,
                method = HttpMethod.valueOf(requestVo.method),
                headers = requestHeaderDescriptor.map { it.field to (it.value.joinToString(",")) }.toMap(),
                descriptors = requestBodyDescriptor,
                uriVar = mapOf())

        return ok(executeResult)
    }

}