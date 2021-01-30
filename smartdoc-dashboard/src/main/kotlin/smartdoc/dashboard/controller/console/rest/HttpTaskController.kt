package smartdoc.dashboard.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import restdoc.client.api.model.HttpInvocation
import restdoc.client.api.model.HttpInvocationResult
import restdoc.client.api.model.InvocationResult
import smartdoc.dashboard.controller.console.model.RequestDto
import smartdoc.dashboard.controller.console.model.RestWebInvocationResultVO
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.model.HttpApiTestLog
import smartdoc.dashboard.model.HttpTaskExecutor
import smartdoc.dashboard.model.TestMode
import smartdoc.dashboard.util.PathValue

import java.net.URL
import java.util.*
import javax.validation.Valid

@RequestMapping("/httptask")
@RestController
class HttpTaskController {

    @Autowired
    private lateinit var httpTaskExecutor: HttpTaskExecutor

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @PostMapping("/submit")
    fun submitHttpTask(@RequestBody @Valid dto: RequestDto): Any {

        val log = log(dto)
        val startTime = Date().time
        log.url = dto.url

        val res = if (dto.remoteAddress != null) {
            val result = remoteInvoke(dto)
            log.testMode = TestMode.RPC
            log.remote = dto.remoteAddress
            result
        } else {
            if (!dto.url.startsWith("http") && !dto.url.startsWith("https"))
                Status.BAD_REQUEST.error("请填写完整的API请求地址")
            val result = publicNetExecuteTask(dto)
            log.testMode = TestMode.PUBLIC_NET
            val url = URL(dto.url)
            log.remote = "${url.protocol}://${url.host}:${url.port}"
            result
        }

        log.responseStatus = res.status
        log.testDurationTimeMill = Date().time - startTime
        log.responseHeader = res.responseHeaders
        log.responseBody = res.responseBody
        log.success = (log.responseStatus == 200)
        log.queryParameters = null

        if (dto.documentId != null && dto.documentId!!.isNotBlank()) {
            mongoTemplate.save(log)
        }

        val vo = RestWebInvocationResultVO(
                isSuccessful = res.successful,
                exceptionMsg = res.exceptionMsg,
                invocation = res.invocation,
                status = res.status,
                responseHeaders = res.responseHeaders,
                responseBody = res.responseBody,
                queryParam = log.queryParameters
        )

        return ok(vo)
    }

    // TODO  API check
    private fun remoteInvoke(dto: RequestDto): HttpInvocationResult {

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val bodyMap = smartdoc.dashboard.projector.JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()

        val invocation = HttpInvocation().apply {
            url = dto.lookupPath()
            method = dto.method
            requestHeaders = requestHeaderDescriptor.map { bd -> bd.field to listOf<String>(bd.value) }.toMap().toMutableMap()
            queryParam = smartdoc.dashboard.util.URLUtil.parseQueryParam(  dto.url)
            requestBody = bodyMap
            uriVariable = uriVarDescriptor.map { it.field to it.value }.toMap().toMutableMap()
        }

        return try {
            HttpInvocationResult()
        } catch (e: Throwable) {
            e.printStackTrace()
            HttpInvocationResult(
                    false,
                    e.message,
                    invocation,
                    -1,
                    mutableMapOf(),
                    null
            )
        }
    }

    private fun publicNetExecuteTask(dto: RequestDto): HttpInvocationResult {

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val bodyMap = smartdoc.dashboard.projector.JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()

        val restWebInvocation = HttpInvocation()
                .apply {
                    url = dto.lookupPath()
                    method = dto.method
                    requestHeaders = requestHeaderDescriptor.map { bd -> bd.field to listOf(bd.value) }.toMap().toMutableMap()
                    queryParam = smartdoc.dashboard.util.URLUtil.parseQueryParam(dto.url)
                    requestBody = bodyMap
                    uriVariable = uriVarDescriptor.map { it.field to it.value }.toMap().toMutableMap()
                }

        var invocationResult: InvocationResult
        try {
            val responseEntity = httpTaskExecutor.execute(restWebInvocation)

            invocationResult = HttpInvocationResult().apply {
                successful = true
                status = responseEntity?.statusCodeValue ?: -1
                responseHeaders = responseEntity?.headers?.map { it.key to it.value }?.toMap()?.toMutableMap()
                        ?: mutableMapOf()
                responseBody = responseEntity?.body
                invocation = restWebInvocation
            }

            return invocationResult
        } catch (e: Throwable) {
            invocationResult = when (e) {
                is HttpServerErrorException.BadGateway -> HttpInvocationResult(false, "BadGateway", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.BadRequest -> HttpInvocationResult(false, "BadRequest", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Conflict -> HttpInvocationResult(false, "Conflict", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Forbidden -> HttpInvocationResult(false, "Forbidden", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.GatewayTimeout -> HttpInvocationResult(false, "GatewayTimeout", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Gone -> HttpInvocationResult(false, "Gone", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.NotFound -> HttpInvocationResult(false, "NotFound", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.MethodNotAllowed -> HttpInvocationResult(false, "MethodNotAllowed", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.NotAcceptable -> HttpInvocationResult(false, "NotAcceptable", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.UnsupportedMediaType -> HttpInvocationResult(false, "UnsupportedMediaType", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.UnprocessableEntity -> HttpInvocationResult(false, "UnprocessableEntity", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.TooManyRequests -> HttpInvocationResult(false, "TooManyRequests", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Unauthorized -> HttpInvocationResult(false, "Unauthorized", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.InternalServerError -> HttpInvocationResult(false, "InternalServerError", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.NotImplemented -> HttpInvocationResult(false, "NotImplemented", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.ServiceUnavailable -> HttpInvocationResult(false, "ServiceUnavailable", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                else -> HttpInvocationResult(false, "未知错误${e.message}", restWebInvocation, -1, mutableMapOf(), null)
            }
            return invocationResult
        }
    }

    /**
     * @see restdoc.web.model.HttpApiTestLog
     */
    private fun log(dto: RequestDto): HttpApiTestLog {
        val log = HttpApiTestLog()
        log.apply {
            id = smartdoc.dashboard.util.IDUtil.id()
            documentId = dto.documentId
            uriParameters = dto.uriFields?.map { it.field!! to it.value!! }?.toMap()
            requestHeaderParameters = dto.headers?.map { it.headerKey to it.headerValue }?.toMap()
            requestBodyParameters = smartdoc.dashboard.projector.JsonProjector(dto.mapToRequestDescriptor().map { PathValue(it.path, it.value) }).projectToMap()
            queryParameters = smartdoc.dashboard.util.URLUtil.parseQueryParam(dto.url)
            url = dto.url
        }
        return log
    }
}