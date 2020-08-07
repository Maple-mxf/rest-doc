package restdoc.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import restdoc.model.BodyFieldDescriptor
import restdoc.model.FieldType

/**
 * The Bump provided bump JSON tree to flatten array
 * @see List
 *
 *
 * @property jsonString bump target json string
 * @property mapper jackson format json utils
 * @property tree from jsonString convert the json tree
 *
 *
 * @since 1.0
 */
class Bump(private val jsonString: String) {

    private val mapper: ObjectMapper = ObjectMapper()

    private var tree: JsonNode

    private val fieldDescriptor: MutableList<BodyFieldDescriptor> = mutableListOf()

    init {
        tree = mapper.readTree(this.jsonString)
    }

    /**
     * @see restdoc.model.BodyFieldDescriptor
     * @return multi field descriptor
     */
    fun bump(): List<BodyFieldDescriptor> {
        bumpTree("", this.tree)
        return this.fieldDescriptor
    }

    /**
     * Method for bump array nodes
     * @param treeNode tree node
     * @param path field current location path of json tree
     */
    private fun bumpTree(path: String, treeNode: JsonNode) {
        if (treeNode.isNull) {
            this.fieldDescriptor.add(transformValue(path, treeNode))
        } else if (treeNode.isArray) {
            val array = treeNode as ArrayNode
            for (subNode in array) {
                bumpTree("$path[]", subNode)
            }
        } else {
            for (field: MutableMap.MutableEntry<String, JsonNode> in treeNode.fields()) {
                if (field.value.isNull) {
                    this.fieldDescriptor.add(transformValue("${path}.${field.key}", field.value))
                } else {
                    if (field.value.isObject) {
                        bumpTree("${path}.${field.key}", field.value)
                    }
                }
            }
        }
    }

    /**
     * Method for transform field object
     */
    private fun transformValue(
            path: String,
            node: JsonNode
    ): BodyFieldDescriptor {

        val fieldType = when (node.nodeType) {
            JsonNodeType.ARRAY -> FieldType.ARRAY
            JsonNodeType.BINARY -> FieldType.OBJECT
            JsonNodeType.OBJECT -> FieldType.OBJECT
            JsonNodeType.NUMBER -> FieldType.NUMBER
            JsonNodeType.NULL -> FieldType.MISSING
            JsonNodeType.POJO -> FieldType.OBJECT
            JsonNodeType.STRING -> FieldType.STRING
            JsonNodeType.BOOLEAN -> FieldType.BOOLEAN
            else -> FieldType.MISSING
        }

        return BodyFieldDescriptor(
                path = path,
                value = node.toString(),
                description = "",
                type = fieldType,
                optional = false,
                defaultValue = "无可选值")
    }

}