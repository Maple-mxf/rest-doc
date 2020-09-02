package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
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
import restdoc.model.DocType
import restdoc.model.Document
import restdoc.model.ExecuteResult
import restdoc.model.Project
import restdoc.repository.DocumentRepository
import restdoc.repository.ProjectRepository
import restdoc.util.IDUtil
import restdoc.util.JsonDeProjector
import restdoc.web.obj.CreateUpdateWikiDto
import restdoc.web.obj.RequestDto
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
    lateinit var documentRepository: DocumentRepository

    @Autowired
    lateinit var holderKit: HolderKit

    @Autowired
    lateinit var delegate: ExecutorDelegate

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
        requestDto.autocomplete()
        val requestHeaderDescriptor = requestDto.mapToHeaderDescriptor()
        val requestBodyDescriptor = requestDto.mapToRequestDescriptor()
        val responseBodyDescriptor = requestDto.mapToResponseDescriptor()
        val uriVarDescriptor = requestDto.mapToURIVarDescriptor()

        // Save An Api Project Document
        val document = Document(
                id = IDUtil.id(),
                name = requestDto.name,
                projectId = requestDto.projectId,
                resource = requestDto.resource,
                url = requestDto.url,
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                method = HttpMethod.valueOf(requestDto.method),
                description = requestDto.description,
                uriVariables = uriVarDescriptor,
                executeResult = requestDto.executeResult,
                docType = DocType.API)

        documentRepository.save(document)

        return ok(document.id)
    }

    @PutMapping("")
    fun patch(@RequestBody @Valid requestDto: RequestDto): Result {

        if (requestDto.documentId == null) return failure(Status.INVALID_REQUEST, "缺少ID参数")

        requestDto.autocomplete()
        val requestHeaderDescriptor = requestDto.mapToHeaderDescriptor()
        val requestBodyDescriptor = requestDto.mapToRequestDescriptor()
        val responseBodyDescriptor = requestDto.mapToResponseDescriptor()
        val uriVarDescriptor = requestDto.mapToURIVarDescriptor()

        // Save An Api Project Document
        val document = Document(
                id = requestDto.documentId,
                name = requestDto.name,
                projectId = present,
                resource = requestDto.resource,
                url = requestDto.url,
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                method = HttpMethod.valueOf(requestDto.method),
                uriVariables = uriVarDescriptor,
                executeResult = requestDto.executeResult)

        val updateResult = documentRepository.update(document)

        println(updateResult)

        return ok(document.id)
    }


    @PostMapping("/project")
    fun projector(@RequestBody requestDto: RequestDto): Result {
        return ok()
    }

    @PostMapping("/deProject")
    fun deProjector(@RequestBody tree: JsonNode): Result = ok(JsonDeProjector(tree).deProject())

    private val present: String = "Default"

    @PostMapping("/httpTask/submit")
    fun submitHttpTask(@RequestBody @Valid requestDto: RequestDto): Result {

        requestDto.autocomplete()
        val requestHeaderDescriptor = requestDto.mapToHeaderDescriptor()
        val requestBodyDescriptor = requestDto.mapToRequestDescriptor()

        val taskId = IDUtil.id()

        try {
            val executeResult = delegate.execute(
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

    @PostMapping("/wiki")
    fun createWikiDocument(@RequestBody dto: CreateUpdateWikiDto): Result {
        var save = false;
        if (dto.id == null || dto.id!!.isEmpty()) {
            save = true
            dto.id = IDUtil.id()
        }

        val document = Document(
                id = dto.id,
                projectId = dto.projectId,
                name = dto.name,
                resource = dto.resource,
                url = "",
                requestHeaderDescriptor = null,
                requestBodyDescriptor = null,
                responseBodyDescriptors = null,
                uriVariables = null,
                content = dto.content,
                docType = DocType.WIKI
        )

        if (save) {
            documentRepository.save(document)
        } else {
            documentRepository.update(document);
        }

        return ok(document.id)
    }


    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Result {
        documentRepository.deleteById(id)
        return ok()
    }
}