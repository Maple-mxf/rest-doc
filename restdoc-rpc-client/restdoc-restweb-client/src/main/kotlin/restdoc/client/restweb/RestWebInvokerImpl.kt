package restdoc.client.restweb

import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import restdoc.client.api.Invoker
import restdoc.client.api.model.InvocationResult
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult

/**
 * RestWebInvokerImpl
 */
class RestWebInvokerImpl(environment: Environment, private val restTemplate: RestTemplate) : Invoker<RestWebInvocation> {

    private val port: Int = environment.getProperty("server.port", "8080").toInt()
    private val contextPath: String = environment.getProperty("server.servlet.context-path", "")

    override fun rpcInvoke(t: RestWebInvocation): InvocationResult {
        val url = autocompleteURL(t.url)
        val requestHeaders = HttpHeaders()

        t.requestHeaders.forEach { (k, v) -> requestHeaders.addAll(k, v) }
        val httpEntity = HttpEntity(t.requestBody, requestHeaders)

        val responseEntity: ResponseEntity<Any>?
        return try {
            responseEntity = restTemplate.exchange(url, HttpMethod.valueOf(t.method), httpEntity, Any::class.java, t.uriVariable)

            RestWebInvocationResult(true, null,
                    t, responseEntity.statusCodeValue,
                    mutableMapOf(),
                    responseEntity.body)

        } catch (e: RestClientException) {
            when (e) {
                is HttpServerErrorException.BadGateway -> RestWebInvocationResult(false, "BadGateway", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.BadRequest -> RestWebInvocationResult(false, "BadRequest", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Conflict -> RestWebInvocationResult(false, "Conflict", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Forbidden -> RestWebInvocationResult(false, "Forbidden", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.GatewayTimeout -> RestWebInvocationResult(false, "GatewayTimeout", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Gone -> RestWebInvocationResult(false, "Gone", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.NotFound -> RestWebInvocationResult(false, "NotFound", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.MethodNotAllowed -> RestWebInvocationResult(false, "MethodNotAllowed", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.NotAcceptable -> RestWebInvocationResult(false, "NotAcceptable", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.UnsupportedMediaType -> RestWebInvocationResult(false, "UnsupportedMediaType", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.UnprocessableEntity -> RestWebInvocationResult(false, "UnprocessableEntity", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.TooManyRequests -> RestWebInvocationResult(false, "TooManyRequests", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpClientErrorException.Unauthorized -> RestWebInvocationResult(false, "Unauthorized", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.InternalServerError -> RestWebInvocationResult(false, "InternalServerError", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.NotImplemented -> RestWebInvocationResult(false, "NotImplemented", t, e.rawStatusCode, mutableMapOf(), null)
                is HttpServerErrorException.ServiceUnavailable -> RestWebInvocationResult(false, "ServiceUnavailable", t, e.rawStatusCode, mutableMapOf(), null)
                else -> RestWebInvocationResult(false, "未知错误${e.message}", t, -1, mutableMapOf(), null)
            }
        } catch (e: RuntimeException) {
            RestWebInvocationResult(false, "未知错误${e.message}", t, -1, mutableMapOf(), null)
        }
    }

    private fun autocompleteURL(originURL: String): String {
        return if (originURL.startsWith("http") || originURL.startsWith("https")) originURL
        else {
            if (originURL.startsWith(contextPath)) {
                String.format("http://127.0.0.1:%d%s", port, originURL)
            } else {
                if (originURL.startsWith("/")) {
                    String.format("http://127.0.0.1:%d%s%s", port, contextPath, originURL)
                } else {
                    String.format("http://127.0.0.1:%d%s/%s", port, contextPath, originURL)
                }
            }
        }
    }
}