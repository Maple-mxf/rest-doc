package restdoc.web.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod

/**
 * @sample HttpHeaders
 */
@Document(collection = "restdoc_document")
data class Document(
        @Id var id: String?,
        var projectId: String?,
        var name: String?,
        var resource: String,
        val url: String,
        val description: String? = null,
        var requestHeaderDescriptor: List<HeaderFieldDescriptor>?,
        var requestBodyDescriptor: List<BodyFieldDescriptor>?,
        var responseBodyDescriptors: List<BodyFieldDescriptor>?,
        val method: HttpMethod = HttpMethod.GET,
        val uriVarDescriptors: List<URIVarDescriptor>?,
        val executeResult: Map<String, Any?>? = null,
        val content: String? = null,
        var responseHeaderDescriptor: List<HeaderFieldDescriptor>? = null,
        val docType: DocType = DocType.API
)