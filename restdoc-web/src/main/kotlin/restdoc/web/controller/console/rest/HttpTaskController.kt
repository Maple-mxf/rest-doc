package restdoc.web.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import restdoc.client.api.model.InvocationResult
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult
import restdoc.web.controller.console.model.RequestDto
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.model.HttpApiTestLog
import restdoc.web.model.HttpTaskExecutor
import restdoc.web.model.TestMode
import restdoc.web.util.IDUtil
import restdoc.web.util.PathValue
import restdoc.web.util.dp.JsonProjector
import java.util.*
import java.util.regex.Pattern.compile
import javax.validation.Valid

@RequestMapping("/httptask")
@RestController
class HttpTaskController {

    @Autowired
    private lateinit var httpTaskExecutor: HttpTaskExecutor

    @Autowired
    private lateinit var scheduleController: ScheduleController

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @PostMapping("/submit")
    fun submitHttpTask(@RequestBody @Valid dto: RequestDto): Any {

        val log = log(dto)
        val startTime = Date().time
        log.url = dto.url

        val res = if (dto.remoteAddress != null) {
            val result = rpcExecuteTask(dto)
            log.testMode = TestMode.RPC
            result
        } else {
            if (!dto.url.startsWith("http") && !dto.url.startsWith("https"))
                Status.BAD_REQUEST.error("请填写完整的API请求地址")
            val result = publicNetExecuteTask(dto)
            log.testMode = TestMode.PUBLIC_NET

            val p = compile("(\\d+\\.\\d+\\.\\d+\\.\\d+)\\:(\\d+)")
            val matcher = p.matcher(dto.url)
            if (matcher.find()) log.remote = matcher.group(1) + ":" + matcher.group(2)
            result
        }

        log.responseStatus = res.status
        log.testDurationTimeMill = Date().time - startTime
        log.responseHeader = res.responseHeaders
        log.responseBody = res.responseBody
        log.success = (log.responseStatus == 200)

        if (dto.documentId != null && dto.documentId!!.isNotBlank()) {
            mongoTemplate.save(log)
        }

        return ok(res)
    }

    private fun rpcExecuteTask(dto: RequestDto): RestWebInvocationResult {
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
            val executeResult = scheduleController
                    .syncSubmitRemoteHttpTask(dto.remoteAddress!!.replaceFirst("tcp://", ""), taskId, invocation)

            return executeResult
        } catch (e: Throwable) {
            e.printStackTrace()
            return RestWebInvocationResult(
                    isSuccessful = false,
                    exceptionMsg = e.message,
                    status = -1,
                    invocation = invocation,
                    responseHeaders = mutableMapOf(),
                    responseBody = null
            )
        }
    }

    private fun publicNetExecuteTask(dto: RequestDto): RestWebInvocationResult {

        val requestHeaderDescriptor = dto.mapToHeaderDescriptor()
        val requestBodyDescriptor = dto.mapToRequestDescriptor()
        val uriVarDescriptor = dto.mapToURIVarDescriptor()

        val bodyMap = JsonProjector(requestBodyDescriptor.map { PathValue(it.path, it.value) }).projectToMap()

        val restWebInvocation = RestWebInvocation()
                .apply {
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
                responseHeaders = responseEntity?.headers?.map { it.key to it.value }?.toMap()?.toMutableMap()
                        ?: mutableMapOf()
                responseBody = responseEntity?.body
                invocation = restWebInvocation
            }

            return invocationResult
        } catch (e: Throwable) {
            invocationResult = when (e) {
                is HttpServerErrorException.BadGateway -> RestWebInvocationResult(false, "BadGateway", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.BadRequest -> RestWebInvocationResult(false, "BadRequest", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Conflict -> RestWebInvocationResult(false, "Conflict", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Forbidden -> RestWebInvocationResult(false, "Forbidden", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.GatewayTimeout -> RestWebInvocationResult(false, "GatewayTimeout", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Gone -> RestWebInvocationResult(false, "Gone", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.NotFound -> RestWebInvocationResult(false, "NotFound", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.MethodNotAllowed -> RestWebInvocationResult(false, "MethodNotAllowed", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.NotAcceptable -> RestWebInvocationResult(false, "NotAcceptable", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.UnsupportedMediaType -> RestWebInvocationResult(false, "UnsupportedMediaType", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.UnprocessableEntity -> RestWebInvocationResult(false, "UnprocessableEntity", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.TooManyRequests -> RestWebInvocationResult(false, "TooManyRequests", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Unauthorized -> RestWebInvocationResult(false, "Unauthorized", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.InternalServerError -> RestWebInvocationResult(false, "InternalServerError", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.NotImplemented -> RestWebInvocationResult(false, "NotImplemented", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.ServiceUnavailable -> RestWebInvocationResult(false, "ServiceUnavailable", restWebInvocation, e.rawStatusCode, mutableMapOf(), null)
                else -> RestWebInvocationResult(false, "未知错误${e.message}", restWebInvocation, -1, mutableMapOf(), null)
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
            id = IDUtil.id()
            documentId = dto.documentId
            uriParameters = dto.uriFields?.map { it.field!! to it.value!! }?.toMap()
            queryParameters = dto.queryParams?.map { it.key to it.value }?.toMap()
            requestHeaderParameters = dto.headers?.map { it.headerKey to it.headerValue }?.toMap()
            requestBodyParameters = JsonProjector(dto.mapToRequestDescriptor().map { PathValue(it.path, it.value) }).projectToMap()
            url = dto.url
        }
        return log
    }
}