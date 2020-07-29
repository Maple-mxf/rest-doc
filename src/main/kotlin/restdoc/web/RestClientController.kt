package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.Client
import restdoc.core.config.RestDocProperties
import restdoc.model.ApiDocument
import restdoc.model.RequestProcess
import restdoc.model.RequestProcessorDescriptor

@RestController
@RequestMapping("doc")
class RestClientController(
        @Autowired val client: Client,
        @Autowired val mapper: ObjectMapper,
        @Autowired val docConfig: RestDocProperties,
        @Autowired val applicationContext: ApplicationContext,
        @Autowired val mongoTemplate: MongoTemplate
) {

    @RequestMapping("/echo")
    fun echo(@RequestBody body: Map<String, Any>): Any {

        println(mapper.writeValueAsString(body))

        return mapOf("code" to "success")
    }

    @PostMapping("")
    fun save(@RequestBody apiDocument: ApiDocument): Any {
        mongoTemplate.save(apiDocument);
        return mapOf("code" to "success")
    }

    @RequestMapping("/execute")
    fun execute(@RequestBody jsonDescriptor: RequestProcessorDescriptor): Any {

        val header: Map<String, List<String>> = jsonDescriptor.header.map { it.field to it.value }.toMap()
        val body: Map<String, Any>? = jsonDescriptor.body?.map { f -> f.path to f.value }?.toMap()
        val uriVar = jsonDescriptor.uriVariables?.map { it.field to String.format("%s", it.value) }?.toMap()
        val typeReference = ParameterizedTypeReference.forType<JsonNode>(JsonNode::class.java)

        val requestProcess = RequestProcess(
                jsonDescriptor.url,
                header,
                body,
                jsonDescriptor.method,
                uriVar,
                typeReference)

        client.process(requestProcess)

        return mapOf("code" to "success")
    }

    fun storageRequestObj(jsonDescriptor: RequestProcessorDescriptor) {

        // docConfig.metaDirClasspath
    }
}