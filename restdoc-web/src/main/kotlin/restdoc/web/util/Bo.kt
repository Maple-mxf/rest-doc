package restdoc.web.util

import restdoc.model.FieldType

/**
 * Path and value
 */
data class PathValue(var path: String, val value: Any?)


/**
 * Node convert to node tree
 */
data class Node(var path: String, val value: Any?, var type: FieldType, val children: MutableList<Node> = mutableListOf())