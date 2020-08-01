package restdoc.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.model.RequestProcess
import java.net.URI
import java.nio.charset.StandardCharsets

@Component
class Client(
        @Autowired val mapper: ObjectMapper,
        @Autowired val restClient: RestTemplate) {

    fun <R> process(rp: RequestProcess<R>): ResponseEntity<R>? {

        rp.body.ifNull { rp.body = mapper.createObjectNode() }

        val entity: RequestEntity<JsonNode> =
                constructPostEntity(rp.url, rp.uriVariables, rp.header, rp.body!!)

        println(mapper.writeValueAsString(entity))

        val responseEntity = restClient.exchange(entity, rp.parameterizedTypeReference)

        println(mapper.writeValueAsString(responseEntity))

        return null;
    }


    fun constructPostEntity(
            url: String,
            uriVariables: Map<String, Any>?,
            headers: Map<String, List<String>>?,
            body: JsonNode

    ): RequestEntity<JsonNode> {

        val uri: URI = restClient.uriTemplateHandler.expand(url, uriVariables)

        val mt = MediaType.APPLICATION_JSON.toString()

        val request: RequestEntity<JsonNode> = RequestEntity
                .post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.parseMediaType(mt))
                .acceptCharset(StandardCharsets.UTF_8)
                .body(body)

        return request
    }
}




