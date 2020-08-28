package restdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
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
import restdoc.model.*
import restdoc.repository.ProjectRepository
import restdoc.util.IDUtil
import restdoc.web.obj.RequestDto
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
    fun create(@RequestBody @Valid requestDto: RequestDto): Result {

        // Handler Url
        if (!requestDto.url.startsWith("http") &&
                !requestDto.url.startsWith("https")) {
            requestDto.url = String.format("%s%s", "http://", requestDto.url)
        }

        val requestHeaderDescriptor = requestDto.headers
                .filter { it.headerKey.isNotBlank() }
                .map {
                    HeaderFieldDescriptor(
                            field = it.headerKey,
                            value = it.headerValue.split(","),
                            description = it.headerDescription,
                            optional = it.headerConstraint)
                }

        val requestBodyDescriptor = requestDto.requestBody
                .filter { it.requestFieldPath.isNotBlank() }
                .map {
                    BodyFieldDescriptor(
                            path = it.requestFieldPath,
                            value = it.requestFieldValue,
                            description = it.requestFieldDescription,
                            type = FieldType.OBJECT,
                            optional = it.requestFieldConstraint,
                            defaultValue = null
                    )
                }

        val responseBodyDescriptor = requestDto.responseBody
                .filter { it.responseFieldPath.isNotBlank() }
                .map {
                    BodyFieldDescriptor(
                            path = it.responseFieldPath,
                            value = it.responseFieldValue,
                            description = it.responseFieldDescription,
                            type = FieldType.OBJECT,
                            optional = it.responseFieldConstraint,
                            defaultValue = null
                    )
                }

        // Save An Api Project Document
        val document = Document(
                id = IDUtil.id(),
                name = requestDto.name,
                projectId = present,
                resource = requestDto.resource,
                url = requestDto.url,
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                method = HttpMethod.valueOf(requestDto.method),
                uriVariables = null,
                expectResponseHeaders = null,
                expectResponseBody = null,
                executeResult = requestDto.executeResult)

        mongoTemplate.save(document)

        return ok(document.id)
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


    @PostMapping("/project")
    fun projector(@RequestBody requestDto: RequestDto): Result {
        return ok()
    }

    private val present: String = "Default"


    @PostMapping("/httpTask/submit")
    fun submitHttpTask(@RequestBody @Valid requestDto: RequestDto): Result {

        // Handler Url
        if (!requestDto.url.startsWith("http") &&
                !requestDto.url.startsWith("https")) {
            requestDto.url = String.format("%s%s", "http://", requestDto.url)
        }

        val requestHeaderDescriptor = requestDto.headers
                .filter { it.headerKey.isNotBlank() }
                .map {
                    HeaderFieldDescriptor(
                            field = it.headerKey,
                            value = it.headerValue.split(","),
                            description = it.headerDescription,
                            optional = it.headerConstraint)
                }

        val requestBodyDescriptor = requestDto.requestBody
                .filter { it.requestFieldPath.isNotBlank() }
                .map {
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

        try {
            val executeResult = delete.execute(
                    url = requestDto.url,
                    method = HttpMethod.valueOf(requestDto.method),
                    headers = requestHeaderDescriptor.map { it.field to (it.value.joinToString(",")) }.toMap(),
                    descriptors = requestBodyDescriptor,
                    uriVar = mapOf())

            redisTemplate.opsForValue().set(taskId, executeResult)
            redisTemplate.expire(taskId, 1000, TimeUnit.SECONDS)
        } catch (e: Throwable) {
            return failure(Status.BAD_REQUEST, e.message.toString())
        }
        return ok(taskId)
    }

    @GetMapping("/httpTask/{taskId}")
    fun execute(@PathVariable taskId: String): Result {
        val result = redisTemplate.opsForValue().get(taskId) ?: return failure(Status.INVALID_REQUEST, "请刷新页面重试")

        val map = result as LinkedHashMap<String, Any>
        val executeResult = mapper.convertValue(map, ExecuteResult::class.java)
        val responseHeader = executeResult.responseHeader
                .map {
                    val value: List<Any> = it.value as List<Any>
                    it.key to value.joinToString(",")
                }
                .toMap()
        executeResult.responseHeader = responseHeader
        return ok(executeResult)
    }
}