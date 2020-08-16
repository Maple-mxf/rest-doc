package restdoc.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import restdoc.model.BodyFieldDescriptor

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

                            var childNode = mapper.createArrayNode()

                            val ias = Regex("((\\[\\d+\\])+)").find(path)?.let { index ->
                                val str = index.groupValues[index.groupValues.size - 2]
                                val indexArray = str.split("]")
                                        .map { t -> t.replace("[", "").replace("]", "") }
                                        .map { t -> t.toInt() }
                                indexArray
                            }

                            node.putPOJO(it, childNode)
                            var tmpNode: ArrayNode = childNode

                            ias?.forEach { index ->
                                tmpNode[index] = mapper.createArrayNode()
                                tmpNode = childNode[index] as ArrayNode
                            }
                        }
            }
            // If Path is Json
            else {

            }
        }
    }

}