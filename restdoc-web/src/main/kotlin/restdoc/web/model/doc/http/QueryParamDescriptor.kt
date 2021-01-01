package restdoc.web.model.doc.http

data class QueryParamDescriptor(
        val field: String,
        var value: Any,
        var description: String? = null
)