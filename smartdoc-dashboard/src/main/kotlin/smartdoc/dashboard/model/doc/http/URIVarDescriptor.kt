package smartdoc.dashboard.model.doc.http

data class URIVarDescriptor(
        val field: String,
        var value: Any?,
        var description: String? = null
)