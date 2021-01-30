package smartdoc.dashboard.util


/**
 * Path and value
 */
@Deprecated(message = "PathValue")
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
@Deprecated(message = "Node")
data class Node(var path: String, val value: Any?, var type: FieldType, val children: MutableList<Node> = mutableListOf())