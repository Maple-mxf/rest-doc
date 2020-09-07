package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "restdoc_menu")
data class Menu(
        @Id val id: Int,
        val title: String,
        val type: Int,
        val openType: String? = "_iframe",
        val icon: String,
        val href: String = "",
        val children: MutableList<Menu>? = null
)