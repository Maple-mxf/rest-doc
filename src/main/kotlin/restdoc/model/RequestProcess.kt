package restdoc.model

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import restdoc.core.Version

@Suppress("unchecked")
fun <R> responseType(method: HttpMethod): ParameterizedTypeReference<R> {

    val parameterizedTypeReference = when (method) {

        HttpMethod.GET -> ParameterizedTypeReference.forType<String>(String::class.java)

        HttpMethod.TRACE,
        HttpMethod.HEAD,
        HttpMethod.PUT,
        HttpMethod.POST,
        HttpMethod.DELETE,
        HttpMethod.PATCH,
        HttpMethod.OPTIONS
        -> ParameterizedTypeReference.forType<JsonNode>(JsonNode::class.java)

        else -> ParameterizedTypeReference.forType<JsonNode>(JsonNode::class.java)
    }

    return parameterizedTypeReference as ParameterizedTypeReference<R>
}

/**
 * @sample ParameterizedTypeReference.forType
 */
@Deprecated(message = "It is error")
@restdoc.core.Since(Version.V1)
data class RequestProcess<R>(
        val url: String,
        var header: Map<String, List<String>>,
        var body: Map<String, Any>?,
        val method: HttpMethod = HttpMethod.GET,
        val uriVariables: Map<String, String>?,
        val parameterizedTypeReference: ParameterizedTypeReference<R>
)

/**
 * @sample JSONF
 */
@restdoc.core.Since(Version.V1)
enum class FieldType {
    STRING, NUMBER, OBJECT, ARRAY
}


/**
 * @constructor
 */
@restdoc.core.Since(Version.V1)
data class ParameterDescriptor(
        val path: String,
        val type: FieldType = FieldType.OBJECT,
        val defaultsValue: String = "无默认值",
        val sampleValue: Any = "无示例值",
        val explain: String = "",
        val optional: Boolean = true
)





















