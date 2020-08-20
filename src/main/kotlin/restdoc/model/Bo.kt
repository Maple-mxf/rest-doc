package restdoc.model

/**
 * Path and value
 */
data class PathValue(val path: String, val value: Any?)


/**
 * Node convert to node tree
 */
data class Node(val path: String, val value: Any?, val type: FieldType, val children: MutableList<Node> = mutableListOf())