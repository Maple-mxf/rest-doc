package restdoc.client.restweb.invoke

import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import restdoc.remoting.common.body.HttpCommunicationCaptureBody
import java.util.*

open class HttpInvoker(private val restTemplate: RestTemplate, environment: Environment) {
    private val port: Int
    private val contextPath: String
    fun execute(capture: HttpCommunicationCaptureBody): ResponseEntity<Any> {
        val url = autocompleteURL(capture.url)
        capture.completeUrl = url
        val requestHeaders = HttpHeaders()
        capture.requestHeader.forEach { (key: String?, value: List<String?>?) -> requestHeaders[key] = value }
        val httpEntity = HttpEntity(capture.requestBody, requestHeaders)
        return restTemplate.exchange(url, capture.method, httpEntity, Any::class.java, capture.uriVariables)
    }

    private fun autocompleteURL(originURL: String): String {
        if (originURL.startsWith("http") || originURL.startsWith("https")) return originURL
        return if (originURL.startsWith(contextPath)) {
            String.format("http://127.0.0.1:%d%s", port, originURL)
        } else {
            if (originURL.startsWith("/")) {
                String.format("http://127.0.0.1:%d%s%s", port, contextPath, originURL)
            } else {
                String.format("http://127.0.0.1:%d%s/%s", port, contextPath, originURL)
            }
        }
    }

    init {
        port = Objects.requireNonNull(environment.getProperty("server.port")).toInt()
        contextPath = environment.getProperty("server.servlet.context-path")
    }
}