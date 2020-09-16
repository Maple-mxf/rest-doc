package restdoc.client.restweb

import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import restdoc.client.api.Invoker
import restdoc.client.api.model.InvocationResult
import restdoc.client.api.model.RestWebInvocation
import restdoc.client.api.model.RestWebInvocationResult

class RestWebInvokerImpl(environment: Environment, private val restTemplate: RestTemplate) : Invoker<RestWebInvocation> {

    private val port: Int = environment.getProperty("server.port", "8080").toInt()
    private val contextPath: String = environment.getProperty("server.servlet.context-path", "")

    override fun rpcInvoke(t: RestWebInvocation): InvocationResult {
        val url = autocompleteURL(t.url)
        val requestHeaders = HttpHeaders()

        t.requestHeaders.forEach { (k, v) -> requestHeaders.addAll(k, v) }
        val httpEntity = HttpEntity(t.requestBody, requestHeaders)
        
        try {
            val responseEntity = restTemplate.exchange(url, HttpMethod.valueOf(t.method), httpEntity, Any::class.java, t.uriVariable)

            return RestWebInvocationResult(true, null,
                    t, responseEntity.statusCodeValue,
                    mutableMapOf(),
                    responseEntity.body)

        } catch (e: Exception) {
            return RestWebInvocationResult(false, e.message, t, -1, mutableMapOf(), null)
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