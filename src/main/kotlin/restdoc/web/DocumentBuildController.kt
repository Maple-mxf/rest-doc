package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import restdoc.core.Result
import restdoc.core.Status
import restdoc.core.failure
import restdoc.core.ok
import restdoc.model.ApiDocument
import restdoc.model.BodyFieldDescriptor
import restdoc.model.ProjectConfig
import restdoc.model.RequestProcess
import restdoc.util.Client

@Controller
@Deprecated(message = "")
@RequestMapping("/docsBuilder")
class DocumentBuildController(
        @Autowired val mongoTemplate: MongoTemplate,
        @Autowired val client: Client,
        @Autowired val mapper: ObjectMapper
) {

    private val defaultHeader = mapOf(
            "Content-Type" to listOf(MediaType.APPLICATION_JSON_VALUE)
    )

    /**
     * Get index console page
     */
    @GetMapping("")
    fun index(model: Model): String = "index"

    /**
     * Get add api doc fields page
     */
    @GetMapping("/add_view")
    fun addView(model: Model): String = "add"

    @GetMapping("/add_view1")
    fun addView1(model: Model): String = "add1"

    /**
     * Save api field info and record http response result
     * @sample restdoc.Client.process
     */
    @PostMapping("/saveAndExecute/")
    @Deprecated(message = "")
    fun saveAndExecute(@RequestBody apiDocument: ApiDocument): Result {

        // Get project config
        val projectConfig: ProjectConfig = mongoTemplate.findOne(
                Query().addCriteria(Criteria.where("projectId").`is`(apiDocument.projectId)),
                ProjectConfig::class.java) ?: return failure(Status.BAD_REQUEST)

        // Create Header Or Default
        val header: Map<String, List<String>> = apiDocument.requestHeaderDescriptor?.map { it.field to it.value }?.toMap()
                ?: defaultHeader

        // Convert Flatten Data Fields
        val body = apiDocument.requestBodyDescriptor?.let { this.flattenToTree(it) }

        // Build Request Processor
        val requestProcess = RequestProcess<JsonNode>(
                url = "${projectConfig.testURIPrefix}${apiDocument.url}",
                header = header,
                body = body,
                method = apiDocument.method,
                uriVariables = apiDocument.uriVariables?.map { it.field to "${it.value}" }?.toMap(),
                parameterizedTypeReference = ParameterizedTypeReference.forType(JsonNode::class.java))

        // Invoke Api
        val responseEntity = client.process(requestProcess)

        // Expect Response
//        val expectResponse = this.expectResponse(responseEntity, apiDocument.expectResponseHeaders, apiDocument.expectResponseBody)

        // Save given the api document
        mongoTemplate.save(apiDocument)

        // Invoke the api
        // client.process()

        return ok()
    }


    /**
     * Convert given flatten field to Tree
     *
     * Object Field name split by '.'
     *
     * Array Field name split by [] sample:users[].name
     *
     *
     * @sample fields = [   {"basic.name","jack"}   ]
     *
     *
     *
     * @sample Map
     */
    fun flattenToTree(fields: List<BodyFieldDescriptor>): JsonNode {
        val isArray = fields.any {
            it.path.startsWith("[]")
        }
        if (isArray)
            return flattenToArrayNode(fields)

        return flattenToJsonNode(fields)
    }

    /**
     * Flatten to Array
     */
    fun flattenToArrayNode(fields: List<BodyFieldDescriptor>): ArrayNode {
        val arrayNode = mapper.createArrayNode()

        val firstLevelNodes = fields.filter { it.path.matches(Regex("[\\[\\]]+[a-zA-Z]+[0,9]+$")) }

        return arrayNode
    }

    /**
     * Flatten to Tree
     */
    fun flattenToJsonNode(fields: List<BodyFieldDescriptor>): JsonNode {
        return mapper.createObjectNode()
    }
}
