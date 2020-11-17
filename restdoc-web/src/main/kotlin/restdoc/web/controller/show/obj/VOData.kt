package restdoc.web.controller.show.obj

data class NavbarVO(val id: String, val text: String, val icon: String? = null, val href: String?, val hasChildren: Int = 0)