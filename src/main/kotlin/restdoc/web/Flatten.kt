package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jdk.nashorn.internal.ir.ObjectNode
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
     *@param fields Body fields array
     */
    fun flattenToArrayNode(fields: List<BodyFieldDescriptor>): List<JSONFieldNode> {
        val fieldsPath: Set<String> = fields.flatMap {
            val els = it.path.split(".").reversed()
            els.mapIndexed { index, _ -> els.subList(index, els.size).reversed().joinToString("/") }
        }.toSet()

        val paths = fieldsPath.toList()

        val outNodes: List<JSONFieldNode> = fieldsPath
                .filter { it.matches(Regex("^(\\[\\])?[a-zA-Z]+[0-9]?(\\[\\])?$")) }
                .map { JSONFieldNode(path = it, children = null, type = null) }
                .map {
                    getNodeValue(it, paths)
                    it
                }

        buildArrayNode(outNodes)

        return outNodes
    }

    fun buildArrayNode(outNodes: List<JSONFieldNode>) {
        val arrayNode = mapper.createArrayNode()
        for (outNode in outNodes) {
            buildJsonNode(outNode)
        }
    }

    fun buildJsonNode(outNode: JSONFieldNode) {
        val paths = outNode.path.split("/")
        val currentPath = paths.last()

        val matchResult = Regex("(a-zA-Z)+").find(currentPath)
        val field = matchResult?.groupValues?.last()

        val prefixIndex = field?.let { currentPath.indexOf(it) }
        val prefix = prefixIndex?.let { currentPath.substring(0, it) }

        val backendIndex = prefixIndex?.plus(field.length)?.minus(1)
        val backend = backendIndex?.let { currentPath.substring(it) }

        val prefixTagLength = prefix?.let { Regex("^(\\[\\])?$").find(it)?.groupValues?.size }
        val backendTagLength = backend?.let { Regex("^(\\[\\])?$").find(it)?.groupValues?.size }

        println(prefixTagLength)
        println(backendTagLength)
    }

    fun buildJsonNode1(outNode: JSONFieldNode, on: ObjectNode) {
        val paths = outNode.path.split("/")
        val currentPath = paths.last()

        val path = currentPath.replace("[]", "")
        val index = currentPath.indexOf(path)

        val preTags = currentPath.substring(0, index)
        val backTags = currentPath.substring(index + path.length)

        val preTagSize = preTags.split("[").filter { it.isNotBlank() }.size
        val backTagSize = backTags.split("[").filter { it.isNotBlank() }.size

        for (i in preTagSize downTo 0 step 1) {
        }
    }


    /**
     *
     */
    private fun getNodeValue(jsonField: JSONFieldNode, fields: List<String>) {
        val start = jsonField.path.replace("[", "").replace("]", "")

        val childrenNodes = fields.filter {
            val tmp = it.replace("[", "").replace("]", "")
            val regex = "^(\\[\\])?${start}(\\[\\])?[/][a-zA-Z]+[0-9]?(\\[\\])?$"
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