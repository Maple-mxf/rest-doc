package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import restdoc.model.BodyFieldDescriptor


@Component
class Flatten {

    @Autowired
    lateinit var mapper: ObjectMapper

    fun flattenToTree(fields: List<BodyFieldDescriptor>): JsonNode {
        val isArray = fields.any {
            it.path.startsWith("[]")
        }
        if (isArray)
            return flattenToArrayNode(fields)

        return flattenToJsonNode(fields)
    }

    /**
     *a.b.c
     *
     * a/b/c
     * a/b
     * c
     */
    fun flattenToArrayNode(fields: List<BodyFieldDescriptor>): ArrayNode {

        val arrayNode = mapper.createArrayNode()

        fields.map { it.path }.flatMap { path ->

            val els = path.split("\\.")

            els.map { el ->
                val index = els.indexOf(el)

                els.subList(index, els.size - 1).joinToString { "/" }
            }
        }

        val outNodes = fields.filter { it.path.matches(Regex("[\\[\\]]+[a-zA-Z]+[0,9]+$")) }


        return arrayNode
    }

    fun getNodeValue(path: String, fields: List<BodyFieldDescriptor>) {

    }


    fun flattenToJsonNode(fields: List<BodyFieldDescriptor>): JsonNode {
        val objectNode = mapper.createObjectNode()


        return objectNode
    }

}