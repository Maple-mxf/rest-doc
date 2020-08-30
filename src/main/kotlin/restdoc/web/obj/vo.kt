package restdoc.web.obj


val ROOT_NAV: NavNode = NavNode(
        id = "root",
        title = "一级目录",
        field = "title",
        children = mutableListOf(),
        href = null,
        pid = "0",
        checked = true)


fun findChild(parentNode: NavNode, navNodes: List<NavNode>) {
    val children = navNodes.filter { it.pid.equals(parentNode.id) }
    parentNode.children = children
    for (child in children) {
        findChild(child, navNodes)
    }
}

data class NavNode(var id: String,
                   var title: String,
                   var field: String?,
                   var children: List<NavNode>?,
                   var href: String? = null,
                   var pid: String,
                   var spread: Boolean = true,
                   var checked: Boolean = false,
                   var disabled: Boolean = false

)