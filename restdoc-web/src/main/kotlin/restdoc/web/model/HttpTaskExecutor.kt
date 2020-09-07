package restdoc.web.model

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.remoting.common.body.HttpCommunicationCapture

@Component
class HttpTaskExecutor {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    fun execute(capture: HttpCommunicationCapture): ResponseEntity<Any?>? {
        val httpEntity = HttpEntity(capture.requestBody, capture.requestHeaders)
        return restTemplate.exchange(capture.url, capture.method, httpEntity, Any::class.java, capture.uriVariables)
    }
}