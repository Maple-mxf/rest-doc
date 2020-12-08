package restdoc.web.model.doc.http

data class HeaderFieldDescriptor(
        val field: String,
        var value: List<String>,
        var description: String?,
        val optional: Boolean = false
)