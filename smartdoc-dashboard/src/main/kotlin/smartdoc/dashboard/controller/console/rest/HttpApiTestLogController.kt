package smartdoc.dashboard.controller.console.rest

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.web.bind.annotation.*
import restdoc.client.api.model.HttpInvocation
import restdoc.client.api.model.HttpInvocationResult
import smartdoc.dashboard.controller.console.model.BatchDeleteDto
import smartdoc.dashboard.controller.console.model.HttpApiTestLogDeProjectVO
import smartdoc.dashboard.controller.console.model.LayuiPageDto
import smartdoc.dashboard.controller.console.model.layuiTableOK
import smartdoc.dashboard.core.Result
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.repository.HttpApiTestLogRepository

@RestController
class HttpApiTestLogController {

    @Autowired
    private lateinit var httpApiTestLogRepository: HttpApiTestLogRepository

    @Autowired
    private lateinit var mapper: ObjectMapper

    @smartdoc.dashboard.base.auth.Verify(role = ["*"])
    @RequestMapping("/document/{documentId}/httpapitestlog/page")
    fun page(dto: LayuiPageDto, @PathVariable documentId: String): Any {
        val query = Query(Criteria("documentId").`is`(documentId))
        query.with(Sort.by(Sort.Order.desc("createTime")))
        val page = httpApiTestLogRepository.page(query, dto.toPageable())
        return layuiTableOK(data = page.content, count = page.totalElements.toInt())
    }

    @smartdoc.dashboard.base.auth.Verify(role = ["*"])
    @PostMapping("/document/httpapitestlog/{id}/deProject")
    fun deProjectLogData(@PathVariable id: String): Result {

        val log = httpApiTestLogRepository.findById(id).orElseThrow { Status.BAD_REQUEST.instanceError() }

        val responseBodyParameters =
                if (log.responseBody != null)
                    mapper.convertValue(log.responseBody, JsonNode::class.java)
                else null

        val vo = HttpApiTestLogDeProjectVO(
                method = log.method!!,
                url = log.url!!,
                uriParameters = log.uriParameters,
                requestHeaderParameters = log.requestHeaderParameters,
                requestBodyParameters = log.requestBodyParameters,
                responseBodyParameters = responseBodyParameters,
                responseHeaderParameters = log.responseHeader)

        return ok(vo)
    }

    /**
     *
     */
    @smartdoc.dashboard.base.auth.Verify(role = ["SYS_ADMIN"])
    @DeleteMapping("/document/httpapitestlog/batch")
    fun batchDelete(@RequestBody dto: BatchDeleteDto): Result {
        val deleteResult = httpApiTestLogRepository.delete(Query(Criteria("id").`in`(dto.ids)))
        return ok()
    }

    @smartdoc.dashboard.base.auth.Verify(role = ["SYS_ADMIN"])
    @DeleteMapping("/document/httpapitestlog/{id}")
    fun delete(@PathVariable id: String): Result {
        httpApiTestLogRepository.deleteById(id)
        return ok()
    }

    @smartdoc.dashboard.base.auth.Verify(role = ["*"])
    @PostMapping("/document/httpapitestlog/{id}/log2testresult")
    fun log2TestResult(@PathVariable id: String): Result {
        val log = httpApiTestLogRepository.findById(id).orElseThrow { Status.BAD_REQUEST.instanceError() }

        val invocationResult = HttpInvocationResult()
        invocationResult.apply {
            successful = log.success
            status = log.responseStatus
            responseHeaders = if (log.responseHeader != null) log.responseHeader!!.map { it.key to it.value.toMutableList() }.toMap().toMutableMap() else mutableMapOf()
            responseBody = log.responseBody

            val restWebInvocation = HttpInvocation()
            restWebInvocation.apply {
                this.method = log.method!!.name
                this.url = log.url!!
                this.queryParam = if (log.queryParameters != null) log!!.queryParameters!!.toMutableMap() else mutableMapOf()
                this.uriVariable = if (log.uriParameters != null) log!!.uriParameters!!.toMutableMap() else mutableMapOf()
                this.requestHeaders = if (log.requestHeaderParameters != null) log.requestHeaderParameters!!.entries.map { it.key to it.value.split(",") }.toMap().toMutableMap() else mutableMapOf()
                this.requestBody = if (log.requestBodyParameters != null) log.requestBodyParameters!!.toMutableMap() else mutableMapOf()
            }
            invocation = restWebInvocation
        }

        return ok(invocationResult)
    }
}