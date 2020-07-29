package restdoc.model

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod

@Document(collation = "restdoc_api_doc")
data class ApiDocument(
        val resource: String,
        val group: String,
        val name: String,
        val url: String,
        var header: Map<String, List<String>>?,
        var body: Map<String, Any>?,
        val method: HttpMethod = HttpMethod.GET,
        val uriVariables: Map<String, String>?
)

data class CodeResource(val code: String, val sourceType: FieldType)