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

        val fieldsPath: Set<String> = fields.flatMap {
            val els = it.path.split(".").reversed()
            els.mapIndexed { index, _ ->
                els.subList(index, els.size).reversed().joinToString("/")
            }
        }.toSet()

        val paths = fieldsPath.toList()

        val outNodes: List<JSONFieldNode> = fieldsPath
                .map { it.replace("[]", "") }
                .filter { it.matches(Regex(String.format("^%s$", it))) }
                .map { JSONFieldNode(path = it, children = null) }
                .map {
                    getNodeValue(it, paths)
                    it
                }

        println(mapper.writeValueAsString(outNodes))

        return arrayNode
    }

    /**
     *
     */
    fun getNodeValue(jsonField: JSONFieldNode, fields: List<String>) {
        val childrenNodes = fields.filter {
            val start = jsonField.path.replace("[", "").replace("]", "")
            val regex = "^${start}(\\[\\])+[/][a-zA-Z]+[0-9]?(\\[\\])?$"
            it.matches(Regex(regex))
        }.map { JSONFieldNode(path = it, children = null) }

        jsonField.children = childrenNodes
        if (childrenNodes.isEmpty()) return

        for (child: JSONFieldNode in childrenNodes) {
            getNodeValue(child, fields)
        }
    }

    fun flattenToJsonNode(fields: List<BodyFieldDescriptor>): JsonNode {
        val objectNode = mapper.createObjectNode()


        return objectNode
    }

}