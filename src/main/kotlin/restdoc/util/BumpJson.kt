package restdoc.util

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
@Deprecated(message = "")
class BumpJson {

    private val tree: JsonNode

    constructor(jsonString: String) {
        tree = mapper.readTree(jsonString)
    }

    constructor(json: Map<String, Any>) {
        tree = mapper.convertValue(json, JsonNode::class.java)
    }

    constructor(tree: JsonNode) {
        this.tree = tree
    }

    private val mapper: ObjectMapper = ObjectMapper()

    private val fieldDescriptor: MutableList<BodyFieldDescriptor> = mutableListOf()

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
        when {
            treeNode.isNull -> {
                this.fieldDescriptor.add(transformValue(path, treeNode))
            }
            treeNode.isArray -> {
                val array = treeNode as ArrayNode
                for (subNode in array) {
                    bumpTree(path, subNode)
                }
            }
            treeNode.isObject -> {
                for (field: MutableMap.MutableEntry<String, JsonNode> in treeNode.fields()) {
                    when {
                        field.value.isNull ->
                            this.fieldDescriptor.add(transformValue(joinTreePath(path, field.key), field.value))
                        field.value.isObject ->
                            bumpTree(joinTreePath(path, field.key), field.value)
                        field.value.isArray ->
                            bumpTree(joinTreePath(path, "${field.key}[]"), field.value)
                        else -> {
                            this.fieldDescriptor.add(transformValue(joinTreePath(path, field.key), field.value))
                        }
                    }
                }
            }
            else -> this.fieldDescriptor.add(transformValue(path, treeNode))
        }
    }

    private fun joinTreePath(prefix: String, suffix: String): String {
        if (prefix == "") return suffix
        return "${prefix}.${suffix}"
    }

    /**
     * Method for transform field object
     */
    private fun transformValue(
            path: String,
            node: JsonNode
    ): BodyFieldDescriptor {

        val value: Any?

        val fieldType = when (node.nodeType) {
            JsonNodeType.NUMBER -> {
                value = node.numberValue()
                FieldType.NUMBER
            }
            JsonNodeType.NULL -> {
                value = null
                FieldType.MISSING
            }
            JsonNodeType.STRING -> {
                value = node.textValue()
                FieldType.STRING
            }
            JsonNodeType.BOOLEAN -> {
                value = node.booleanValue()
                FieldType.BOOLEAN
            }
            else -> {
                value = null
                FieldType.MISSING
            }
        }
        return BodyFieldDescriptor(
                path = path,
                value = value,
                description = "",
                type = fieldType,
                optional = false,
                defaultValue = "无可选值")
    }

}