package smartdoc.dashboard.model.doc.http


data class HeaderFieldDescriptor(
        val field: String,
        var value: String,
        var description: String? = null,
        val optional: Boolean = false
)