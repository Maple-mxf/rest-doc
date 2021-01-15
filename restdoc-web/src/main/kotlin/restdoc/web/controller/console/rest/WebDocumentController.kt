package restdoc.web.controller.console.rest

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort.Order.desc
import org.springframework.data.domain.Sort.by
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.web.bind.annotation.*
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.rpc.client.common.model.http.HttpApiDescriptor
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.*
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.failure
import restdoc.web.core.ok
import restdoc.web.model.ANY_ROLE
import restdoc.web.model.Project
import restdoc.web.model.Resource
import restdoc.web.model.SYS_ADMIN
import restdoc.web.model.doc.DocType
import restdoc.web.model.doc.http.*
import restdoc.web.repository.ProjectRepository
import restdoc.web.repository.ResourceRepository
import restdoc.web.repository.RestWebDocumentRepository
import restdoc.web.util.FieldType
import restdoc.web.util.IDUtil.id
import restdoc.web.util.IDUtil.now
import restdoc.web.util.MD5Util
import java.net.URL
import java.util.*
import javax.validation.Valid

/**
 * WebDocumentController
 *
 * @author Maple
 * @see Project
 */
@RestController
@RequestMapping("/document")
class WebDocumentController {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var projectRepository: ProjectRepository

    @Autowired
    private lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    private lateinit var resourceRepository: ResourceRepository

    @GetMapping("/list/{projectId}")
    fun list(@PathVariable projectId: String): Result {
        val query = Query().addCriteria(Criteria("projectId").`is`(projectId))
        query.with(by(desc("createTime")))
        return ok(projectRepository.list(query))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Result {
        val doc = restWebDocumentRepository.findById(id).orElseThrow { Status.INVALID_REQUEST.instanceError("id参数错误") }
        return ok(transformRestDocumentToVO(doc))
    }

    private fun extractRawPath(url: String): String {
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

    @PostMapping("")
    @Verify(role = [SYS_ADMIN])
    fun create(@RequestBody @Valid dto: RequestDto): Result {

        dto.url = dto.lookupPath()

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val responseBodyDescriptor = dto.mapToResponseDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()
        val responseHeaderDescriptor = dto.mapToResponseHeaderDescriptor()
        val queryParamDescriptor = dto.mapToQueryParamDescriptor()

        val document = RestWebDocument(
                id = id(),
                name = dto.name,
                projectId = dto.projectId,
                resource = dto.resource!!,
                url = extractRawPath(dto.url),
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                responseHeaderDescriptor = responseHeaderDescriptor,
                queryParamDescriptors = queryParamDescriptor,
                method = HttpMethod.valueOf(dto.method),
                description = dto.description,
                uriVarDescriptors = uriVarDescriptor,
                docType = DocType.API)

        restWebDocumentRepository.save(document)

        GlobalScope.launch {
            optimizationAndAutocomplete(dto.projectId, document)
        }

        return ok(document.id)
    }

    @Verify(role = [SYS_ADMIN])
    @PutMapping("")
    fun patch(@RequestBody @Valid dto: RequestDto): Result {

        if (dto.documentId == null) return failure(Status.INVALID_REQUEST, "缺少ID参数")

        val oldDocument = restWebDocumentRepository.findById(dto.documentId!!)
                .orElseThrow { Status.BAD_REQUEST.instanceError("文档不存在") }

        dto.url = dto.lookupPath()
        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val responseBodyDescriptor = dto.mapToResponseDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()
        val responseHeaderDescriptor = dto.mapToResponseHeaderDescriptor()
        val queryParamDescriptor = dto.mapToQueryParamDescriptor()


        oldDocument.queryParamDescriptors?.forEach {
            queryParamDescriptor
                    .filter { d -> d.field == it.field }
                    .forEach { d ->
                        d.description = it.description
                    }
        }

        oldDocument.requestHeaderDescriptor?.forEach {
            requestHeaderDescriptor
                    .filter { d -> d.field == it.field }
                    .forEach { d -> d.description = it.description }
        }

        oldDocument.requestBodyDescriptor?.forEach {
            requestBodyDescriptor
                    .filter { d -> d.path == it.path }
                    .forEach { d -> d.description = it.description }
        }

        oldDocument.responseBodyDescriptors?.forEach {
            responseBodyDescriptor
                    .filter { d -> d.path == it.path }
                    .forEach { d -> d.description = it.description }
        }

        oldDocument.uriVarDescriptors?.forEach {
            uriVarDescriptor
                    .filter { d -> d.field == it.field }
                    .forEach { d -> d.description = it.description }
        }

        oldDocument.responseHeaderDescriptor?.forEach {
            responseHeaderDescriptor
                    .filter { d -> d.field == it.field }
                    .forEach { d -> d.description = it.description }
        }

        // Save An Api Project Document
        val document = RestWebDocument(
                id = dto.documentId,
                name = dto.name,
                projectId = dto.projectId,
                resource = dto.resource!!,
                url = extractRawPath(dto.url),
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestBodyDescriptor = requestBodyDescriptor,
                responseBodyDescriptors = responseBodyDescriptor,
                queryParamDescriptors = queryParamDescriptor,
                responseHeaderDescriptor = responseHeaderDescriptor,
                method = HttpMethod.valueOf(dto.method),
                uriVarDescriptors = uriVarDescriptor,
                description = dto.description,
                lastUpdateTime = Date().time)

        val updateResult = restWebDocumentRepository.update(document)

        if (updateResult.matchedCount > 0) {
            GlobalScope.launch {
                optimizationAndAutocomplete(dto.projectId, document)
            }
        }

        return ok(document.id)
    }

    @GetMapping("/httpTask/{taskId}")
    @Verify(role = [ANY_ROLE])
    fun execute(@PathVariable taskId: String): Result {
        val result = redisTemplate.opsForValue().get(taskId) ?: return failure(Status.INVALID_REQUEST, "请刷新页面重试")
        val map = result as LinkedHashMap<String, Any>
        return ok(map)
    }

    @Verify(role = [SYS_ADMIN])
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): Result {
        restWebDocumentRepository.deleteById(id)
        return ok()
    }

    @PatchMapping("/{id}")
    @Deprecated(message = "patch")
    fun patch(@PathVariable id: String, @RequestBody @Valid dto: UpdateNodeDto): Result {
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(id)),
                Update().set("name", dto.name))
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

    /**
     * Get recommend field description
     */
    private fun getRecommendDescription(projectId: String,
                                        field: String,
                                        type: FieldDescType): String? {

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
                        id = id(),
                        field = it.key,
                        description = it.value.replace("\n", ""),
                        type = FieldDescType.REQUEST_PARAM,
                        projectId = projectId)
            }
        }

        val responseFieldDesc = responseFieldParamMap.let { map ->
            map.map {
                HistoryFieldDescription(
                        id = id(),
                        field = it.key,
                        description = it.value.replace("\n", ""),
                        type = FieldDescType.RESPONSE_PARAM,
                        projectId = projectId)
            }
        }

        val headerFieldDesc = headerFieldParamMap.let { map ->
            map.map {
                HistoryFieldDescription(
                        id = id(),
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
    @Verify(role = [ANY_ROLE])
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
    @Verify(role = [SYS_ADMIN])
    fun patchURIVarsSnippet(@PathVariable id: String,
                            @Valid @RequestBody dto: UpdateURIVarSnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.uriVarDescriptors?.filter { it.field == dto.field }
                ?.forEach {
                    it.value = dto.value
                    it.description = dto.description
                }
        doc.lastUpdateTime = Date().time
        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/{id}/snippet/requestHeader")
    fun patchRequestHeaderSnippet(@PathVariable id: String,
                                  @Valid @RequestBody dto: UpdateRequestHeaderSnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.requestHeaderDescriptor?.filter { it.field == dto.field }
                ?.forEach {
                    it.value = dto.value.split(",")
                    it.description = dto.description
                }
        doc.lastUpdateTime = Date().time
        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/{id}/snippet/requestBody")
    fun patchRequestBodySnippet(@PathVariable id: String,
                                @Valid @RequestBody dto: UpdateRequestBodySnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.requestBodyDescriptor?.filter { it.path == dto.path }
                ?.forEach {
                    it.value = dto.value
                    it.description = dto.description
                }
        doc.lastUpdateTime = Date().time
        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/{id}/snippet/responseBody")
    fun patchResponseBodySnippet(@PathVariable id: String,
                                 @Valid @RequestBody dto: UpdateResponseBodySnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.responseBodyDescriptors?.filter { it.path == dto.path }
                ?.forEach {
                    it.value = dto.value
                    it.description = dto.description
                }
        doc.lastUpdateTime = Date().time
        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }

    @Verify(role = ["SYS_ADMIN"])
    @PatchMapping("/{id}/snippet/description")
    fun patchDescription(@PathVariable id: String,
                         @Valid @RequestBody dto: UpdateDescriptionSnippetDto): Result {

        val doc = restWebDocumentRepository.findById(id).orElseThrow(Status.BAD_REQUEST::instanceError)

        doc.description = dto.description
        doc.lastUpdateTime = Date().time
        restWebDocumentRepository.update(doc)

        return ok(transformRestDocumentToVO(doc))
    }


    @GetMapping("/serviceClient/{clientId}/apiList")
    fun apiList(@PathVariable clientId: String,
                @RequestParam(required = false, defaultValue = "REST_WEB") ap: ApplicationType): Any {

        val rootNav = NavNode(
                id = "root",
                title = "一级目录",
                field = "title",
                children = mutableListOf(),
                href = null,
                pid = "0",
                checked = true)

        if (ApplicationType.REST_WEB == ap) {

//            val restwebAPIList =
//                    this.clientRegistryCenter.getExposedAPIFilterApplicationType(
//                            clientId, ApplicationType.REST_WEB) as Collection<HttpApiDescriptor>

            // TODO  API check
            val restwebAPIList = ArrayList<HttpApiDescriptor>()

            val resources = restwebAPIList
                    .groupBy { it.controller }
                    .map { it.key }
                    .map {
                        Resource(
                                id = it,
                                tag = it,
                                name = it.split('.').last(),
                                pid = rootNav.id,
                                projectId = null,
                                createTime = null,
                                createBy = null
                        )
                    }
            val navNodes = resources.map {
                NavNode(id = it.id!!,
                        title = it.name!!,
                        field = "name",
                        children = null,
                        pid = it.pid!!,
                        checked = true,
                        spread = false
                )
            }

            findChild(rootNav, navNodes)

            val allNode = mutableListOf<NavNode>()

            allNode.add(rootNav)
            allNode.addAll(navNodes)

            val docs = restwebAPIList.map {
                RestWebDocument(
                        id = MD5Util.MD5Encode(it.controller + it.pattern, "UTF-8"),
                        projectId = null,
                        name = it.endpoint.split("#").last(),
                        resource = it.controller,
                        url = it.pattern,
                        description = null,
                        responseBodyDescriptors = null,
                        method = HttpMethod.resolve(it.method),
                        uriVarDescriptors = null,
                        responseHeaderDescriptor = null)
            }

            for (navNode in allNode) {
                val childrenDocNode: MutableList<NavNode> = docs
                        .filter { navNode.id == it.resource }
                        .map {

                            val node = NavNode(
                                    id = it.id!!,
                                    title = it.url,
                                    field = "",
                                    children = mutableListOf(),
                                    href = null,
                                    pid = navNode.id,
                                    spread = true,
                                    checked = true)

                            node.type = if (DocType.API == it.docType) NodeType.API else NodeType.WIKI
                            node
                        }.toMutableList()

                if (navNode.children != null) {
                    navNode.children!!.addAll(childrenDocNode)
                } else {
                    navNode.children = childrenDocNode
                }
            }
            return ok(rootNav.children)

        } else if (ApplicationType.DUBBO == ap) {
        /*    val restwebAPIList = this.clientRegistryCenter.getExposedAPIFilterApplicationTypeByRemote(clientId, ApplicationType.DUBBO)
                    as Collection<DubboApiDescriptor>*/
            throw RuntimeException("Not support application type $ap")
        } else {
            throw RuntimeException("Not support application type $ap")
        }

        return ok()
    }

    @Verify(role = [SYS_ADMIN])
    @PostMapping("/serviceClient/{clientId}/syncApi")
    fun syncServiceInstanceApi(@PathVariable clientId: String, @RequestBody dto: SyncRestApiDto): Any {
//        val apiList =
//                clientRegistryCenter.getExposedAPIFilterApplicationType(clientId, ApplicationType.REST_WEB)
//                        as Collection<HttpApiDescriptor>

        val apiList = ArrayList<HttpApiDescriptor>()

        val map = apiList.groupBy { it.packageName }
                .entries
                .map {
                    it.key to it.value.groupBy { t -> t.controller }
                }
                .toMap()

        val groupByResourceAPIList = apiList.groupBy { it.controller }.toMap()

        val rootNav = NavNode(
                id = "root",
                title = "一级目录",
                field = "title",
                children = mutableListOf(),
                href = null,
                pid = "0",
                checked = true)

        var totalQuantity = 0
        var savedQuantity = 0

        for (controller in groupByResourceAPIList.keys) {
            val resourceId = controller.hashCode().toString()
            val resourceExist = resourceRepository.existsById(resourceId)
            val simpleName = controller.split('.').last()
            if (!resourceExist) {
                val resource = Resource(
                        id = resourceId,
                        tag = controller,
                        name = simpleName,
                        pid = rootNav.id,
                        projectId = dto.projectId,
                        createTime = now(),
                        createBy = ""
                )
                mongoTemplate.save(resource)
            }

            val apiList = groupByResourceAPIList[controller]

            totalQuantity += apiList!!.size

            apiList.forEach { api ->
                val id = MD5Util.MD5Encode(api.controller + api.pattern, "UTF-8")

                if (dto.docIds.contains(id)) {
                    val documentExist = restWebDocumentRepository.existsById(id)
                    if (!documentExist) {
                        savedQuantity++
                        val document = RestWebDocument(
                                id = id,
                                projectId = dto.projectId,
                                name = api.pattern,
                                resource = resourceId,
                                url = api.pattern,
                                description = api.pattern,
                                responseBodyDescriptors = null,
                                method = HttpMethod.resolve(api.method),
                                uriVarDescriptors = null,
                                responseHeaderDescriptor = null,
                                docType = DocType.API)

                        mongoTemplate.save(document)
                    }
                }
            }
        }
        return ok(SyncDocumentResultVo(totalQuantity = totalQuantity, savedQuantity = savedQuantity))
    }

    @Verify(role = [SYS_ADMIN])
    @PostMapping("/emptydoc")
    fun createEmptyApiDoc(@RequestBody dto: CreateEmptyDocDto): Result {
        val document = RestWebDocument(
                id = id(),
                projectId = dto.projectId,
                name = dto.name,
                resource = dto.resourceId,
                url = "",
                description = null,
                responseBodyDescriptors = null,
                uriVarDescriptors = null,
                responseHeaderDescriptor = null,
                docType = dto.docType)

        mongoTemplate.save(document)

        // var json = {"id":data.field.addId,"title": data.field.addNodeName,"parentId": node.nodeId};
        return ok(document)
    }

    @Verify(role = [SYS_ADMIN])
    @PostMapping("/copy")
    fun copyDocument(@RequestBody dto: CopyDocumentDocDto): Result {

        val originDocument = restWebDocumentRepository.findById(dto.documentId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError() }

        val newDocument = RestWebDocument(
                id = id(),
                projectId = originDocument.projectId,
                name = dto.name,
                resource = dto.resourceId,
                url = originDocument.url,
                description = originDocument.description,
                requestHeaderDescriptor = originDocument.requestHeaderDescriptor,
                requestBodyDescriptor = originDocument.requestBodyDescriptor,
                responseBodyDescriptors = originDocument.responseBodyDescriptors,
                uriVarDescriptors = originDocument.uriVarDescriptors,
                queryParamDescriptors = originDocument.queryParamDescriptors,
                responseHeaderDescriptor = originDocument.responseHeaderDescriptor,
                docType = originDocument.docType,
                createTime = Date().time,
                lastUpdateTime = Date().time)

        mongoTemplate.save(newDocument)
        return ok(mapOf(
                "id" to newDocument.id,
                "resource" to newDocument.resource,
                "name" to newDocument.name
        ))
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/baseinfo")
    fun updateBaseInfo(@RequestBody @Valid dto: UpdateNodeDto): Result {
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(dto.id)),
                Update().set("name", dto.name)
                        .set("order", dto.order)
                        .set("resource", dto.pid)
        )

        return ok()
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/uridescriptor")
    fun updateURIVarDescriptor(@RequestBody dto: BatchUpdateURIVarSnippetDto): Result {
        val descriptor = dto.values.map { URIVarDescriptor(field = it.field, value = it.value, description = it.description) }
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(dto.documentId)), Update().set("uriVarDescriptors", descriptor))
        println(updateResult)
        return ok(descriptor)
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/requestbodydescriptor")
    fun updateRequestBodyDescriptor(@RequestBody dto: BatchUpdateRequestBodySnippetDto): Result {
        val descriptor = dto.values
                .map {
                    BodyFieldDescriptor(
                            path = it.path,
                            value = it.value,
                            description = it.description,
                            type = FieldType.valueOf(it.type!!),
                            optional = it.optional)
                }
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(dto.documentId)), Update().set("requestBodyDescriptor", descriptor))
        println(updateResult)
        return ok(descriptor)
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/requestheaderdescriptor")
    fun updateRequestBodyDescriptor(@RequestBody dto: BatchUpdateRequestHeaderSnippetDto): Result {
        val descriptor = dto.values
                .map {
                    HeaderFieldDescriptor(
                            field = it.field,
                            value = it.value.split(","),
                            description = it.description,
                            optional = it.optional)
                }
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(dto.documentId)), Update().set("requestHeaderDescriptor", descriptor))
        println(updateResult)
        return ok(descriptor)
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/responsebodydescriptor")
    fun updateResponseBodyDescriptor(@RequestBody dto: BatchUpdateResponseBodySnippetDto): Result {
        val descriptor = dto.values
                .map {
                    BodyFieldDescriptor(
                            path = it.path,
                            value = it.value,
                            description = it.description,
                            type = FieldType.valueOf(it.type!!))
                }
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(dto.documentId)), Update().set("responseBodyDescriptors", descriptor))
        println(updateResult)
        return ok(descriptor)
    }

    @Verify(role = [SYS_ADMIN])
    @PatchMapping("/queryparamdescriptor")
    fun updateQueryParamDescriptor(@RequestBody dto: BatchUpdateQueryParamSnippetDto): Result {
        val descriptor = dto.values
                .map {
                    QueryParamDescriptor(
                            field = it.field,
                            value = it.value,
                            description = it.description
                    )
                }
        val updateResult = restWebDocumentRepository.update(Query().addCriteria(Criteria("_id").`is`(dto.documentId)),
                Update().set("queryParamDescriptors", descriptor))
        println(updateResult)
        return ok(descriptor)
    }

}

