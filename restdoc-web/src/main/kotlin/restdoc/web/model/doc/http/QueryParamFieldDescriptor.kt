package restdoc.web.model.doc.http

data class QueryParamFieldDescriptor(
        val field: String,
        var value: Any,
        var description: String?,
        val optional: Boolean = false
)