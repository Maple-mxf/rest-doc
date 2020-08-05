package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import org.springframework.stereotype.Component
import restdoc.model.BodyFieldDescriptor
import restdoc.model.JSONFieldNode

/**
 * @since 1.0
 */
@Component
class Flatten {

    private val mapper: ObjectMapper = ObjectMapper()

    fun flattenNodeToTree(fields: List<BodyFieldDescriptor>): JsonNode {
        val isArray = fields.any {
            it.path.startsWith("[]")
        }
        if (isArray)
            return flattenToArrayNode(fields)

        return flattenToJsonNode(fields)
    }


    /**
     *
     */
    fun flattenToArrayNode(fields: List<BodyFieldDescriptor>): ArrayNode {
        val arrayNode = mapper.createArrayNode()

        val fieldsPath: List<String> = fields
                .map { it.path }
                .flatMap { path ->
                    val els: List<String> = path.split(".")
                    path.split(".").mapIndexed { index, _ -> els.subList(index, els.size).joinToString("/") }
                }

        val outNodes: List<JSONFieldNode> = fields
                .filter { it.path.matches(Regex("^(\\[\\])+[a-zA-Z]+[0-9](\\[\\])?+$")) }
                .map { JSONFieldNode(path = it.path, children = null) }
                .map {
                    it.children = getNodeValue(it, fieldsPath)
                    it
                }

        println(mapper.writeValueAsString(outNodes))

        return arrayNode
    }

    /**
     *
     */
    fun getNodeValue(jsonField: JSONFieldNode, fields: List<String>): List<JSONFieldNode> {
        val childrenNodes = fields.filter {
            val start = jsonField.path.replace("[", "").replace("]", "")
            val regex = "^${start}(\\[\\])+[/][a-zA-Z]+[0-9]?(\\[\\])?$"
            it.matches(Regex(regex))
        }.map { JSONFieldNode(path = it, children = null) }

        for (child: JSONFieldNode in childrenNodes) {
            jsonField.children = getNodeValue(child, fields)
        }

        return childrenNodes
    }

    fun flattenToJsonNode(fields: List<BodyFieldDescriptor>): JsonNode {
        val objectNode = mapper.createObjectNode()


        return objectNode
    }

}