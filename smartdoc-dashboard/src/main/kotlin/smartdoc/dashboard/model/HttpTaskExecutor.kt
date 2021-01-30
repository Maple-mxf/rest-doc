package smartdoc.dashboard.model

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.client.api.model.HttpInvocation

@Component
class HttpTaskExecutor {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    fun execute(invocation: HttpInvocation): ResponseEntity<Any?>? {
        val requestHeaders = HttpHeaders()
        invocation.requestHeaders.forEach { (k, v) -> requestHeaders.addAll(k, v) }
        val httpEntity = HttpEntity(invocation.requestBody, requestHeaders)
        return restTemplate.exchange(invocation.url, HttpMethod.valueOf(invocation.method), httpEntity, Any::class.java, invocation.uriVariable)
    }
}