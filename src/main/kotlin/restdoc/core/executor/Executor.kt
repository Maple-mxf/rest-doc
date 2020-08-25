package restdoc.core.executor

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.model.BodyFieldDescriptor
import restdoc.model.ExecuteResult
import restdoc.util.JsonProjector
import restdoc.util.PathValue

interface Executor {

    fun execute(request: Request): ResponseEntity<JsonNode>

    fun method(): HttpMethod
}

abstract class AbstractExecutor : Executor {
    @Autowired
    lateinit var restTemplate: RestTemplate

    @Autowired
    lateinit var delegate: ExecutorDelegate

    init {
        delegate.cache[this.method()] = this
    }

    fun constructHeaders(headerMap: Map<String, String>): HttpHeaders {
        val headers = HttpHeaders()
        for (entry in headerMap) headers.set(entry.key, entry.value)
        return headers
    }
}

abstract class AbstractSimpleExecutor : AbstractExecutor() {


    override fun execute(request: Request): ResponseEntity<JsonNode> {
        val headers = this.constructHeaders(request.header)
        val httpEntity = HttpEntity(request.content, headers)

        val queryString = request.content.fields().asSequence()
                .map { String.format("%s=%s", it.key, it.value) }
                .joinToString(separator = "&")

        request.url = String.format("%s?%s", request.url, queryString)

        return restTemplate.exchange<JsonNode>(
                request.url,
                request.method,
                httpEntity,
                JsonNode::class.java,
                request.pathVariable)
    }


}

@Component
open class GetRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.GET
    }
}

@Component
open class PostRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.POST
    }
}

@Component
open class DeleteRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.DELETE
    }
}

@Component
open class PatchRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.PATCH
    }
}

@Component
open class PutRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.PUT
    }
}

@Component
open class OptionsRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.OPTIONS
    }
}

@Component
open class TraceRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.TRACE
    }
}

@Component
open class HeadRequestExecutor : AbstractSimpleExecutor() {
    override fun method(): HttpMethod {
        return HttpMethod.HEAD
    }
}

/**
 * @see RestTemplate.exchange
 */
@Component
open class ExecutorDelegate {

    @Autowired
    lateinit var restTemplate: RestTemplate

    val cache: MutableMap<HttpMethod, Executor> = mutableMapOf()

    fun execute(url: String,
                uriVar: Map<String, Any>,
                method: HttpMethod,
                headers: Map<String, String>,
                descriptors: List<BodyFieldDescriptor>): ExecuteResult {

        val content = JsonProjector(descriptors.map { PathValue(it.path, it.value) })
                .project()

        val request = Request(
                url = url,
                method = method,
                pathVariable = uriVar,
                header = headers,
                content = content)

        val executor = cache[method]
        
        val url: String = restTemplate.uriTemplateHandler.expand(url, uriVar).toASCIIString()

        if (executor != null) {
            val entity: ResponseEntity<JsonNode> = executor.execute(request)

            val responseHeader = entity.headers
            val responseBody = entity.body

            return ExecuteResult(
                    status = entity.statusCodeValue,
                    method = method.name,
                    url = url,
                    requestHeader = headers,
                    responseHeader = responseHeader,
                    content = content,
                    body = responseBody
            )
        }

        return ExecuteResult(
                status = 400,
                method = method.name,
                url = url,
                requestHeader = headers,
                responseHeader = mapOf(),
                content = content,
                body = null
        )
    }
}

