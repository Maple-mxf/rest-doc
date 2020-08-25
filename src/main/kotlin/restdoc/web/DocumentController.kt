package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import restdoc.base.auth.HolderKit
import restdoc.core.Result
import restdoc.core.Status
import restdoc.core.executor.ExecutorDelegate
import restdoc.core.failure
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
import java.util.concurrent.TimeUnit
import javax.validation.Valid

/**
 * @see Project
 */
@RestController
@RequestMapping("/document")
//@Verify
class DocumentController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var holderKit: HolderKit

    @Autowired
    lateinit var delete: ExecutorDelegate

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    lateinit var mapper: ObjectMapper

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
    fun deProjector(@RequestBody tree: JsonNode): Result = ok(JsonDeProjector(tree).deProject())


    @PostMapping("/project")
    fun projector(@RequestBody requestVo: RequestVo): Result {
        return ok()
    }


    @PostMapping("/httpTask/submit")
    fun submitHttpTask(@RequestBody @Valid requestVo: RequestVo): Result {
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
                    type = FieldType.OBJECT,
                    optional = it.requestFieldConstraint,
                    defaultValue = null
            )
        }

        val taskId = IDUtil.id()

        GlobalScope.launch {
            val executeResult = delete.execute(
                    url = requestVo.url,
                    method = HttpMethod.valueOf(requestVo.method),
                    headers = requestHeaderDescriptor.map { it.field to (it.value.joinToString(",")) }.toMap(),
                    descriptors = requestBodyDescriptor,
                    uriVar = mapOf())

            redisTemplate.opsForValue().set(taskId, executeResult)

            redisTemplate.expire(taskId, 60, TimeUnit.SECONDS)
        }

        return ok(taskId)
    }

    @GetMapping("/httpTask/{taskId}")
    fun execute(@PathVariable taskId: String): Result {
        val executeResult = redisTemplate.opsForValue().get(taskId) ?: return failure(Status.INVALID_REQUEST)
        return ok(executeResult)
    }
}