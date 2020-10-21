package restdoc.web.controller

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
import restdoc.client.api.model.InvocationResult
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.RestWebExposedAPI
import restdoc.web.controller.obj.*
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.failure
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientExposedAPIManager
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.*
import restdoc.web.repository.ProjectRepository
import restdoc.web.repository.ResourceRepository
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
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var exposedAPIManager: ClientExposedAPIManager

    @Autowired
    private lateinit var httpTaskExecutor: HttpTaskExecutor

    @Autowired
    private lateinit var resourceRepository: ResourceRepository

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
                resource = dto.resource!!,
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

        GlobalScope.launch {
            optimizationAndAutocomplete(dto.projectId, document)
        }

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
                resource = dto.resource!!,
                url = extractRawPath(dto.url, uriVars),
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                method = HttpMethod.valueOf(dto.method),
                uriVarDescriptors = uriVarDescriptor,
                executeResult = dto.executeResult,
                description = dto.description)

        val updateResult = restWebDocumentRepository.update(document)

        if (updateResult.matchedCount > 0) {
            GlobalScope.launch {
                optimizationAndAutocomplete(dto.projectId, document)
            }
        }

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
                Status.BAD_REQUEST.error("请填写完整的API请求地址")
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

    /**
     * 同步Http服务
     */
    @PostMapping("/sync")
    fun syncDocument(@RequestBody dto: SyncApiEmptyTemplateDto): Result {
        val apiList = exposedAPIManager.get(ApplicationType.REST_WEB, dto.service, dto.remoteAddress.replace("tcp://",""))
                as List<RestWebExposedAPI>

        val groupByResourceAPIList = apiList.groupBy { it.controller }.toMap()

        for (controller in groupByResourceAPIList.keys) {
            val resourceId = controller.hashCode().toString()
            val resourceExist = resourceRepository.existsById(resourceId)

            if (!resourceExist) {
                val resource = Resource(
                        id = resourceId,
                        tag = controller,
                        name = controller,
                        pid = ROOT_NAV.id,
                        projectId = dto.projectId,
                        createTime = now(),
                        createBy = ""
                )
                mongoTemplate.save(resource)
            }

            val apiList = groupByResourceAPIList[controller]
            apiList!!.forEach { api ->
                val id = (api.controller + api.pattern + api.supportMethod.joinToString(separator = ",")).hashCode().toString()
                val documentExist = restWebDocumentRepository.existsById(id)
                if (!documentExist) {
                    val document = RestWebDocument(
                            id = id,
                            projectId = dto.projectId,
                            name = api.pattern,
                            resource = resourceId,
                            url = api.pattern,
                            description = api.pattern,
                            requestBodyDescriptor = null,
                            requestHeaderDescriptor = null,
                            responseBodyDescriptors = null,
                            queryParam = null,
                            method = HttpMethod.valueOf(api.supportMethod[0]),
                            uriVarDescriptors = null,
                            executeResult = null,
                            content = null,
                            responseHeaderDescriptor = null,
                            docType = DocType.API)

                    mongoTemplate.save(document)
                }
            }
        }

        return ok()
    }

    private fun optimizationAndAutocomplete(projectId: String, doc: RestWebDocument) {
        try {
            // 1 Optimization
            this.optimization(projectId, doc)

            // 2 Autocomplete
            this.autocomplete(projectId, doc)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Autocomplete field description
     */
    private fun autocomplete(projectId: String, doc: RestWebDocument) {
        // 1 Autocomplete header descriptor
        doc.requestHeaderDescriptor
                ?.filter { it.description == null || it.description!!.isEmpty() }
                ?.forEach {
                    it.description = getRecommendDescription(projectId = projectId, field = it.field, type = FieldDescType.HEADER)
                }

        // 2 Autocomplete request descriptor
        doc.requestBodyDescriptor
                ?.filter { it.description == null || it.description!!.isEmpty() }
                ?.forEach {
                    it.description = getRecommendDescription(projectId = projectId, field = it.path, type = FieldDescType.REQUEST_PARAM)
                }

        // 3 Autocomplete response descriptor
        doc.responseBodyDescriptors
                ?.filter { it.description == null || it.description!!.isEmpty() }
                ?.forEach {
                    it.description = getRecommendDescription(projectId = projectId, field = it.path, type = FieldDescType.RESPONSE_PARAM)
                }

        restWebDocumentRepository.update(doc)
    }

    /*private inner class CountDescription {
        lateinit var _id: String
        var frequency by Delegates.notNull<Long>()
    }*/

    /**
     * Get recommend field description
     */
    private fun getRecommendDescription(projectId: String,
                                        field: String,
                                        type: FieldDescType): String? {

        /*val match = match(Criteria("projectId").`is`(projectId).and("type").`is`(type).and(" field").`is`(field))
        val count = group("description").push("createTime").`as`("createTime").count().`as`("frequency")
        val mappedResults = mongoTemplate.aggregate(newAggregation(match, count),
                HistoryFieldDescription::class.java, CountDescription::class.java).mappedResults*/

        val query = Query().addCriteria(Criteria("projectId").`is`(projectId).and("type").`is`(type).and("field").`is`(field))
                .with(by(desc("frequency")))
        query.limit(1)

        val hfd = mongoTemplate.findOne(query, HistoryFieldDescription::class.java)

        return hfd?.description
    }

    private fun optimization(projectId: String, doc: RestWebDocument) {

        val fieldMap1 = doc.uriVarDescriptors
                ?.filter { it.description != null && it.description!!.isNotEmpty() }
                ?.map { it.field to it.description!! }
                ?.toMap()

        val fieldMap2 = doc.requestBodyDescriptor
                ?.filter { it.description != null && it.description!!.isNotEmpty() }
                ?.map { it.path to it.description!! }
                ?.toMap()

        val fieldMap3 = doc.responseBodyDescriptors
                ?.filter { it.description != null && it.description!!.isNotEmpty() }
                ?.map { it.path to it.description!! }
                ?.toMap()

        val fieldMap4 = doc.requestHeaderDescriptor
                ?.filter { it.description != null && it.description!!.isNotEmpty() }
                ?.map { it.field to it.description!! }
                ?.toMap()

        val requestFieldParamMap = mutableMapOf<String, String>()
        val responseFieldParamMap = mutableMapOf<String, String>()
        val headerFieldParamMap = mutableMapOf<String, String>()

        fieldMap1?.let { requestFieldParamMap.putAll(it) }
        fieldMap2?.let { requestFieldParamMap.putAll(it) }
        fieldMap3?.let { responseFieldParamMap.putAll(it) }
        fieldMap4?.let { headerFieldParamMap.putAll(it) }

        val requestFieldsDesc = requestFieldParamMap.let { map ->
            map.map {
                HistoryFieldDescription(
                        id = IDUtil.id(),
                        field = it.key,
                        description = it.value.replace("\n", ""),
                        type = FieldDescType.REQUEST_PARAM,
                        projectId = projectId)
            }
        }

        val responseFieldDesc = responseFieldParamMap.let { map ->
            map.map {
                HistoryFieldDescription(
                        id = IDUtil.id(),
                        field = it.key,
                        description = it.value.replace("\n", ""),
                        type = FieldDescType.RESPONSE_PARAM,
                        projectId = projectId)
            }
        }

        val headerFieldDesc = headerFieldParamMap.let { map ->
            map.map {
                HistoryFieldDescription(
                        id = IDUtil.id(),
                        field = it.key,
                        description = it.value.replace("\n", "").trim(),
                        type = FieldDescType.HEADER,
                        projectId = projectId)
            }
        }

        val historyFieldsDesc = mutableListOf<HistoryFieldDescription>()
        historyFieldsDesc.addAll(headerFieldDesc)
        historyFieldsDesc.addAll(requestFieldsDesc)
        historyFieldsDesc.addAll(responseFieldDesc)

        historyFieldsDesc.forEach {

            val query = Query().addCriteria(Criteria("projectId").`is`(projectId).and("type").`is`(it.type).and("field").`is`(it.field)
                    .and("description").`is`(it.description))
                    .with(by(desc("frequency")))

            val hfd = mongoTemplate.findOne(query, HistoryFieldDescription::class.java)

            if (hfd != null) {
                mongoTemplate.updateFirst(
                        Query().addCriteria(Criteria("_id").`is`(hfd.id)),
                        Update().set("frequency", hfd.frequency + 1),
                        HistoryFieldDescription::class.java)

            } else mongoTemplate.save(it)
        }
    }

    @GetMapping("/{id}/snippet")
    fun getSnippet(@PathVariable id: String, @RequestParam type: String): LayuiTable {
        val uriVars = restWebDocumentRepository.findById(id)
                .map {
                    when (type) {
                        "uri" -> it.uriVarDescriptors
                        "requestHeader" -> it.requestHeaderDescriptor
                        "requestBody" -> it.requestBodyDescriptor
                        "responseBody" -> it.responseBodyDescriptors
                        else -> listOf()
                    }
                }
                .orElse(mutableListOf())

        return layuiTableOK(uriVars!!, uriVars.size)
    }

    @PatchMapping("/{id}/snippet/uri")
    fun patchURIVarsSnippet(@PathVariable id: String,
                            @Valid @RequestBody dto: UpdateURIVarSnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.uriVarDescriptors?.filter { it.field == dto.field }
                ?.forEach {
                    it.value = dto.value
                    it.description = dto.description
                }

        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @PatchMapping("/{id}/snippet/requestHeader")
    fun patchRequestHeaderSnippet(@PathVariable id: String,
                                  @Valid @RequestBody dto: UpdateRequestHeaderSnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.requestHeaderDescriptor?.filter { it.field == dto.field }
                ?.forEach {
                    it.value = dto.value.split(",")
                    it.description = dto.description
                }

        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @PatchMapping("/{id}/snippet/requestBody")
    fun patchRequestBodySnippet(@PathVariable id: String,
                                @Valid @RequestBody dto: UpdateRequestBodySnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.requestBodyDescriptor?.filter { it.path == dto.path }
                ?.forEach {
                    it.value = dto.value
                    it.description = dto.description
                }

        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @PatchMapping("/{id}/snippet/responseBody")
    fun patchResponseBodySnippet(@PathVariable id: String,
                                 @Valid @RequestBody dto: UpdateResponseBodySnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.responseBodyDescriptors?.filter { it.path == dto.path }
                ?.forEach {
                    it.value = dto.value
                    it.description = dto.description
                }

        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @PatchMapping("/{id}/snippet/description")
    fun patchDescription(@PathVariable id: String,
                         @Valid @RequestBody dto: UpdateDescriptionSnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.description = dto.description
        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }
}

