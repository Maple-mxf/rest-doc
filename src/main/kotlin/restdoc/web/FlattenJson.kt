package restdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import restdoc.model.BodyFieldDescriptor
import java.math.BigDecimal

class FlattenJson(val descriptors: List<BodyFieldDescriptor>) {

    private val mapper: ObjectMapper = ObjectMapper()

    fun flatten(): Any {

        // Find First level nodes
        val outNodes =
                descriptors.filter { it.path.matches(Regex("^[a-zA-Z]+[0-9]?[a-zA-Z]?(\\[0-9\\])?$")) }

        for (outNode in outNodes) {
//            flattenJsonBody(outNode)
        }

        return mapper;
    }

    // a[].b
    private fun flattenJsonBody(outNode: BodyFieldDescriptor, on: ObjectNode) {

        val children = descriptors
                .filter { it.path.matches(Regex("^${outNode.path}[a-zA-Z]+[a-zA-Z0-9]?(\\[\\d+\\])?$")) }

        val pathArray = outNode.path.split(".")

        val node: ObjectNode = on

        for (path in pathArray) {
            // if Path is Array
            if (path.matches(Regex("^[a-zA-Z]+[a-zA-Z0-9]?(\\[\\d+\\])+$"))) {
                Regex("([a-zA-Z]+[a-zA-Z0-9]?)").find(path)?.groupValues?.get(1)
                        ?.let {
                            val childNode = mapper.createArrayNode()
                            val ias = Regex("((\\[\\d+\\])+)").find(path)?.let { index ->
                                val str = index.groupValues[index.groupValues.size - 2]
                                val indexArray = str.split("]")
                                        .map { t -> t.replace("[", "").replace("]", "") }
                                        .map { t -> t.toInt() }
                                indexArray
                            }
                            node.putPOJO(it, childNode)
                            var tmpNode: ArrayNode = childNode
                            ias?.forEachIndexed { index, value ->
                                if (index != ias.size - 1) {
                                    tmpNode[value] = mapper.createArrayNode()
                                    tmpNode = childNode[value] as ArrayNode
                                } else {
                                    tmpNode[value] = createJsonNodeOfType(outNode.value)
                                }
                            }
                        }
            }
            // If Path is Json
            else {

            }
        }
    }

    fun createJsonNodeOfType(value: Any?): ValueNode {
        when (value) {
            is Boolean -> {
                return BooleanNode.valueOf(value)
            }
            is Int -> {
                return IntNode.valueOf(value)
            }
            is Long -> {
                return LongNode.valueOf(value)
            }
            is String -> {
                return TextNode.valueOf(value)
            }
            is Float -> {
                return FloatNode.valueOf(value)
            }
            is Double -> {
                return DoubleNode.valueOf(value)
            }
            is Short -> {
                return ShortNode.valueOf(value)
            }
            is BigDecimal -> {
                return DecimalNode.valueOf(value)
            }
            else -> return MissingNode.getInstance()
        }
    }
}
