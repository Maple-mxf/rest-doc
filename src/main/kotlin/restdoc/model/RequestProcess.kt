package restdoc.model

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod

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
 * @since 1.0
 */
@Deprecated(message = "")
data class RequestProcess<R>(
        val url: String,
        var header: Map<String, List<String>>,
        var body: JsonNode?,
        val method: HttpMethod = HttpMethod.GET,
        val uriVariables: Map<String, String>?,
        val parameterizedTypeReference: ParameterizedTypeReference<R>
)

/**
 * @sample JSON
 * @since 1.0
 */
enum class FieldType {

    STRING {
        override fun isPrimitive(): Boolean {
            return true
        }
    }
    ,
    NUMBER {
        override fun isPrimitive(): Boolean {
            return true
        }
    },
    OBJECT {
        override fun isPrimitive(): Boolean {
            return false
        }
    },
    ARRAY {
        override fun isPrimitive(): Boolean {
            return false
        }
    },
    BOOLEAN {
        override fun isPrimitive(): Boolean {
            return true
        }
    },
    MISSING {
        override fun isPrimitive(): Boolean {
            return true
        }
    };

    abstract fun isPrimitive(): Boolean
}


/**
 * @constructor
 * @since 1.0
 */
@Deprecated(message = "")
data class ParameterDescriptor(
        val path: String,
        val type: FieldType = FieldType.OBJECT,
        val defaultsValue: String = "无默认值",
        val sampleValue: Any = "无示例值",
        val explain: String = "",
        val optional: Boolean = true
)





















