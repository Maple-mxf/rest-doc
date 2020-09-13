package restdoc.web.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import restdoc.remoting.common.body.HttpCommunicationCaptureBody
import restdoc.web.base.auth.HolderKit
import restdoc.web.controller.obj.CreateUpdateWikiDto
import restdoc.web.controller.obj.RequestDto
import restdoc.web.controller.obj.UpdateNodeDto
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.failure
import restdoc.web.core.ok
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.DocType
import restdoc.web.model.Document
import restdoc.web.model.HttpTaskExecutor
import restdoc.web.model.Project
import restdoc.web.repository.DocumentRepository
import restdoc.web.repository.ProjectRepository
import restdoc.web.util.IDUtil
import restdoc.web.util.JsonDeProjector
import restdoc.web.util.JsonProjector
import restdoc.web.util.PathValue
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
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var httpTaskExecutor: HttpTaskExecutor

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

        requestDto.url = requestDto.lookupPath()
        val requestHeaderDescriptor = requestDto.mapToHeaderDescriptor()
        val requestBodyDescriptor = requestDto.mapToRequestDescriptor()
        val responseBodyDescriptor = requestDto.mapToResponseDescriptor()
        val uriVarDescriptor = requestDto.mapToURIVarDescriptor()

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
                uriVarDescriptors = uriVarDescriptor,
                executeResult = requestDto.executeResult,
                docType = DocType.API)

        documentRepository.save(document)

        return ok(document.id)
    }

    @PutMapping("")
    fun patch(@RequestBody @Valid requestDto: RequestDto): Result {

        if (requestDto.documentId == null) return failure(Status.INVALID_REQUEST, "缺少ID参数")

        requestDto.url = requestDto.lookupPath()
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
                uriVarDescriptors = uriVarDescriptor,
                executeResult = requestDto.executeResult)

        val updateResult = documentRepository.update(document)

        return ok(document.id)
    }


    @PostMapping("/project")
    fun projector(@RequestBody requestDto: RequestDto): Result {
        return ok()
    }

    @PostMapping("/deProject")
    fun deProjector(@RequestBody tree: JsonNode): Result = ok(JsonDeProjector(tree).deProject())

    private val present: String = "Default"

    @Autowired
    lateinit var scheduleController: ScheduleController

    @PostMapping("/httpTask/submit")
    fun submitHttpTask(@RequestBody @Valid dto: RequestDto): Result {
        return if (dto.remoteAddress != null) {
            rpcExecuteTask(dto)
        } else {
            outExecuteTask(dto)
        }
    }

    private fun rpcExecuteTask(dto: RequestDto): Result {
        val taskId = IDUtil.id()

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val capture = HttpCommunicationCaptureBody()

        try {
            capture.url = dto.lookupPath()
        } catch (e: Exception) {
            Status.BAD_REQUEST.error("请求路径必须不能携带任何ip和协议")
        }
        capture.method = HttpMethod.valueOf(dto.method)
        val requestHeaders = HttpHeaders()
        requestHeaderDescriptor.forEach { requestHeaders.addAll(it.field, it.value) }
        capture.requestHeaders = requestHeaders

        if (capture.method.equals(HttpMethod.GET)) {
            capture.queryParam = requestBodyDescriptor.map { it.path to it.value.toString() }.toMap().toMutableMap()
        } else {
            capture.requestBody = JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()
        }

        capture.uriVariables = uriVarDescriptor
                .map { it.field to it.value.toString() }
                .toMap()
                .toMutableMap()

        try {
            val executeResult = scheduleController.syncSubmitRemoteHttpTask(
                    dto.remoteAddress,
                    taskId,
                    capture)

            redisTemplate.opsForValue().set(taskId, executeResult)
            redisTemplate.expire(taskId, 1000, TimeUnit.SECONDS)
        } catch (e: Throwable) {
            return failure(Status.BAD_REQUEST, e.message.toString())
        }
        return ok(taskId)
    }


    /**
     * @sample Throwable
     */
    private fun outExecuteTask(dto: RequestDto): Result {

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val taskId = IDUtil.id()

        val capture = HttpCommunicationCaptureBody()
        try {
            capture.url = dto.lookupPath()
        } catch (e: Exception) {
            Status.BAD_REQUEST.error("必须携带http协议或者https协议")
        }

        capture.method = HttpMethod.valueOf(dto.method)

        val requestHeaders = HttpHeaders()
        requestHeaderDescriptor.forEach { requestHeaders.addAll(it.field, it.value) }

        capture.requestHeaders = requestHeaders
                .map { (k, v) -> k to v }
                .toMap().toMutableMap()

        if (capture.method.equals(HttpMethod.GET)) {
            capture.queryParam = requestBodyDescriptor.map { it.path to it.value.toString() }.toMap().toMutableMap()
        } else {
            capture.requestBody = JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()
        }

        capture.uriVariables = uriVarDescriptor
                .map { it.field to it.value.toString() }
                .toMap()
                .toMutableMap()
        try {

            val responseEntity = httpTaskExecutor.execute(capture)

            if (responseEntity == null) {
                capture.status = HttpStatus.NOT_FOUND.value()
            } else {
                capture.status = responseEntity.statusCodeValue
                if (responseEntity.hasBody()) capture.responseBody = responseEntity.body
                capture.responseHeader = responseEntity.headers
//                capture.responseContentType = capture.responseHeader.contentType
            }

            redisTemplate.opsForValue().set(taskId, capture)
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
        return ok(map)
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
                uriVarDescriptors = null,
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

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @RequestBody @Valid dto: UpdateNodeDto): Result {
        val updateResult = documentRepository.update(Query().addCriteria(Criteria("_id").`is`(id)),
                Update().set("name", dto.name))
        return ok()
    }
}