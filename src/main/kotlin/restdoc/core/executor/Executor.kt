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

interface Executor {

    fun execute(request: Request): ResponseEntity<JsonNode>

    fun method(): HttpMethod
}

abstract class AbstractExecutor : Executor {
    @Autowired
    lateinit var restTemplate: RestTemplate

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

        val queryString = request.queryParams
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

    fun execute(url: String,
                uriVar: Map<String, Any>,
                method: HttpMethod,
                descriptors: List<BodyFieldDescriptor>) {

        val uri = restTemplate.uriTemplateHandler.expand(url, uriVar)
        val url = uri.toASCIIString()

        
    }
}

