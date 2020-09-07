package restdoc.web.controller

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
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.failure
import restdoc.web.core.ok
import restdoc.web.model.BodyFieldDescriptor

@Controller
@Deprecated(message = "")
@RequestMapping("/docsBuilder")
class DocumentBuildController(
        @Autowired val mongoTemplate: MongoTemplate,
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
