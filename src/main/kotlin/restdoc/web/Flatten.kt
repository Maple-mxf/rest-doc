package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import restdoc.model.BodyFieldDescriptor
import restdoc.model.JSONFieldNode
import restdoc.model.JSONFieldType

/**
 * @since 1.0
 *
 * The Flatten class provided flatten list to json tree
 *
 * @see Bump bump the json tree to flatten array
 */
@Component
class Flatten {

    private val mapper: ObjectMapper = ObjectMapper()

    fun flattenNodeToTree(fields: List<BodyFieldDescriptor>): Any {
        val isArray = fields.any {
            it.path.startsWith("[]")
        }
        if (isArray) {
            return flattenToArrayNode(fields)
        }
        return flattenToJsonNode(fields)
    }

    /**
     *
     */
    fun flattenToArrayNode(fields: List<BodyFieldDescriptor>): List<JSONFieldNode> {
        val arrayNode = mapper.createArrayNode()

        val fieldsPath: Set<String> = fields.flatMap {
            val els = it.path.split(".").reversed()
            els.mapIndexed { index, _ -> els.subList(index, els.size).reversed().joinToString("/") }
        }.toSet()

        val paths = fieldsPath.toList()

        val outNodes: List<JSONFieldNode> = fieldsPath
                .map { it.replace("[]", "") }
                .filter { it.matches(Regex("^[a-zA-Z]+[0-9]?$")) }
                .map { JSONFieldNode(path = it, children = null, type = null) }
                .map {
                    getNodeValue(it, paths)
                    it
                }

        return outNodes
    }


    /**
     *
     */
    fun getNodeValue(jsonField: JSONFieldNode, fields: List<String>) {
        val start = jsonField.path.replace("[", "").replace("]", "")

        val childrenNodes = fields.filter {
            val tmp = it.replace("[", "").replace("]", "")
            val regex = "^${start}(\\[\\])?[/][a-zA-Z]+[0-9]?(\\[\\])?$"
            val matches = tmp.matches(Regex(regex))
            matches
        }.map { JSONFieldNode(path = it, children = null, type = null) }

        jsonField.children = childrenNodes

        jsonField.type = childrenNodes.any { it.path.matches(Regex("^(\\[\\])?${start}(\\[\\])+.*$")) }
                .let {
                    if (it)
                        JSONFieldType.ARRAY
                    else
                        JSONFieldType.JSON
                }

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