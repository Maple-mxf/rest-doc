package restdoc.web.util

import restdoc.web.model.FieldType


/**
 * Path and value
 */
data class PathValue(var path: String, val value: Any?){
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}


/**
 * Node convert to node tree
 */
data class Node(var path: String, val value: Any?, var type: FieldType, val children: MutableList<Node> = mutableListOf())