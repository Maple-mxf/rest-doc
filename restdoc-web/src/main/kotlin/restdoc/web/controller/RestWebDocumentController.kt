package restdoc.web.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.asc
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import restdoc.client.api.model.InvocationResult
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult
import restdoc.web.controller.obj.CreateUpdateWikiDto
import restdoc.web.controller.obj.RequestDto
import restdoc.web.controller.obj.SyncApiEmptyTemplateDto
import restdoc.web.controller.obj.UpdateNodeDto
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.failure
import restdoc.web.core.ok
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.*
import restdoc.web.repository.ProjectRepository
import restdoc.web.repository.RestWebDocumentRepository
import restdoc.web.util.IDUtil
import restdoc.web.util.IDUtil.now
import restdoc.web.util.JsonDeProjector
import restdoc.web.util.JsonProjector
import restdoc.web.util.PathValue
import java.net.URI
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import javax.validation.Valid

/**
 * @see Project
 */
@RestController
@RequestMapping("/document")
//@Verify
class RestWebDocumentController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    lateinit var restTemplate: RestTemplate

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

    private fun extractRawPath(url: String, uriVars: Map<String, Any>): String {
        return when {
            url.startsWith("http") -> URL(url).path
            url.matches(Regex("^([/][a-zA-Z0-9])+[/]?$")) -> url
            else -> {
                val arr = url.split(delimiters = *arrayOf("/"))
                if (arr.size == 1) arr[0]
                else "/" + arr.subList(1, arr.size).joinToString(separator = "/")
            }
        }
    }

    private fun maintainHistoryAddress(url: String, documentId: String) {
        val uri = URI(url)
        val address = "${uri.scheme}://${uri.authority}"
        val query = Query().addCriteria(Criteria("documentId").`is`(documentId).and("address").`is`(address))

        if (mongoTemplate.exists(query, HistoryAddress::class.java)) return

        val historyAddressNumber = mongoTemplate.count(
                Query().addCriteria(Criteria("documentId").`is`(documentId)),
                HistoryAddress::class.java)

        val ha = HistoryAddress(id = IDUtil.id(), address = address, documentId = documentId, createTime = now())

        if (historyAddressNumber > 10) {
            // delete old
            mongoTemplate.remove(
                    Query().addCriteria(Criteria("documentId").`is`(documentId)).with(by(asc("createTime"))).limit(1),
                    HistoryAddress::class.java)
        } else {
            mongoTemplate.save(ha)
        }
    }

    @PostMapping("")
    fun create(@RequestBody @Valid dto: RequestDto): Result {

        dto.url = dto.lookupPath()
        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val responseBodyDescriptor = dto.mapToResponseDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val uriVars = uriVarDescriptor.map { it.field to it.value }.toMap()

        val document = RestWebDocument(
                id = IDUtil.id(),
                name = dto.name,
                projectId = dto.projectId,
                resource = dto.resource,
                url = extractRawPath(dto.url, uriVars),
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                method = HttpMethod.valueOf(dto.method),
                description = dto.description,
                uriVarDescriptors = uriVarDescriptor,
                executeResult = dto.executeResult,
                docType = DocType.API)

        restWebDocumentRepository.save(document)

        return ok(document.id)
    }

    @PutMapping("")
    fun patch(@RequestBody @Valid dto: RequestDto): Result {

        if (dto.documentId == null) return failure(Status.INVALID_REQUEST, "缺少ID参数")

        dto.url = dto.lookupPath()
        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val responseBodyDescriptor = dto.mapToResponseDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val uriVars = uriVarDescriptor.map { it.field to it.value }.toMap()

        // Save An Api Project Document
        val document = RestWebDocument(
                id = dto.documentId,
                name = dto.name,
                projectId = dto.projectId,
                resource = dto.resource,
                url = extractRawPath(dto.url, uriVars),
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                method = HttpMethod.valueOf(dto.method),
                uriVarDescriptors = uriVarDescriptor,
                executeResult = dto.executeResult,
                description = dto.description)

        val updateResult = restWebDocumentRepository.update(document)

        return ok(document.id)
    }

    @PostMapping("/project")
    fun projector(@RequestBody requestDto: RequestDto): Result {
        return ok()
    }

    @PostMapping("/deProject")
    fun deProjector(@RequestBody tree: JsonNode): Result = ok(JsonDeProjector(tree).deProject())

    @Autowired
    lateinit var scheduleController: ScheduleController

    @PostMapping("/httpTask/submit")
    fun submitHttpTask(@RequestBody @Valid dto: RequestDto): Result {
        return if (dto.remoteAddress != null) {

            if (!dto.remoteAddress!!.matches(Regex("^([/][a-zA-Z0-9])+[/]?$")))
                Status.BAD_REQUEST.error("RPC测试请直接输入项目的contextPath")

            // Record history url address
            maintainHistoryAddress(dto.url, dto.documentId!!)

            rpcExecuteTask(dto)
        } else {
            if (!dto.url.startsWith("http") && !dto.url.startsWith("https"))
                Status.BAD_REQUEST.error("链接无效")
            outExecuteTask(dto)
        }
    }

    private fun rpcExecuteTask(dto: RequestDto): Result {
        val taskId = IDUtil.id()

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val bodyMap = JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()

        val invocation = RestWebInvocation().apply {
            url = dto.lookupPath()
            method = dto.method
            requestHeaders = requestHeaderDescriptor.map { bd -> bd.field to bd.value }.toMap().toMutableMap()
            queryParam = if (dto.queryParams == null) mutableMapOf() else dto.queryParams!!
            requestBody = bodyMap
            uriVariable = uriVarDescriptor.map { it.field to it.value }.toMap().toMutableMap()
        }

        try {
            val executeResult = scheduleController.syncSubmitRemoteHttpTask(dto.remoteAddress, taskId, invocation)
            redisTemplate.opsForValue().set(taskId, executeResult)
            redisTemplate.expire(taskId, 1000, TimeUnit.SECONDS)
        } catch (e: Throwable) {
            return failure(Status.BAD_REQUEST, e.message.toString())
        }
        return ok(taskId)
    }

    private fun outExecuteTask(dto: RequestDto): Result {

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val taskId = IDUtil.id()

        val bodyMap = JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()

        val restWebInvocation = RestWebInvocation().apply {
            url = dto.lookupPath()
            method = dto.method
            requestHeaders = requestHeaderDescriptor.map { bd -> bd.field to bd.value }.toMap().toMutableMap()
            queryParam = if (dto.queryParams == null) mutableMapOf() else dto.queryParams!!
            requestBody = bodyMap
            uriVariable = uriVarDescriptor.map { it.field to it.value }.toMap().toMutableMap()
        }

        var invocationResult: InvocationResult
        try {
            val responseEntity = httpTaskExecutor.execute(restWebInvocation)

            invocationResult = RestWebInvocationResult().apply {
                isSuccessful = true
                status = responseEntity?.statusCodeValue ?: -1
                responseHeaders = responseEntity?.headers ?: mutableMapOf()
                responseBody = responseEntity?.body
                invocation = restWebInvocation
            }

            redisTemplate.opsForValue().set(taskId, invocationResult)
            redisTemplate.expire(taskId, 1000, TimeUnit.SECONDS)
        } catch (e: Throwable) {
            invocationResult = RestWebInvocationResult().apply {
                isSuccessful = false
                exceptionMsg = e.message
                status = -1
                responseHeaders = mutableMapOf()
                responseBody = null
                invocation = restWebInvocation
            }

            redisTemplate.opsForValue().set(taskId, invocationResult)
            redisTemplate.expire(taskId, 1000, TimeUnit.SECONDS)

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
    fun createWiki(@RequestBody dto: CreateUpdateWikiDto): Result {
        var save = false;
        if (dto.id == null || dto.id!!.isEmpty()) {
            save = true
            dto.id = IDUtil.id()
        }

        val document = RestWebDocument(
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
            restWebDocumentRepository.save(document)
        } else {
            restWebDocumentRepository.update(document);
        }

        return ok(document.id)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Result {
        restWebDocumentRepository.deleteById(id)
        return ok()
    }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @RequestBody @Valid dto: UpdateNodeDto): Result {
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(id)),
                Update().set("name", dto.name))
        return ok()
    }

    @PostMapping("/")
    fun syncDocument(@RequestBody dto: SyncApiEmptyTemplateDto): Result {
        // Invoke remote client api info
        val emptyApiTemplates = scheduleController.syncGetEmptyApiTemplates(dto.clientId)


        return ok()
    }
}