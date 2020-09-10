package restdoc.web.controller.obj


val ROOT_NAV: NavNode = NavNode(
        id = "root",
        title = "一级目录",
        field = "title",
        children = mutableListOf(),
        href = null,
        pid = "0",
        checked = true)


fun findChild(parentNode: NavNode, navNodes: List<NavNode>) {
    val children: MutableList<NavNode> = navNodes.filter { it.pid == parentNode.id }.toMutableList()
    parentNode.children = children
    for (child in children) {
        findChild(child, navNodes)
    }
}

enum class NodeType {
    RESOURCE, WIKI, API
}

data class NavNode(var id: String,
                   var title: String,
                   var field: String?,
                   var children: MutableList<NavNode>?,
                   var href: String? = null,
                   var pid: String,
                   var spread: Boolean = true,
                   var checked: Boolean = false,
                   var disabled: Boolean = false,
                   var type: NodeType = NodeType.RESOURCE

)