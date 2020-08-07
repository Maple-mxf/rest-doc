package restdoc.model

import org.springframework.http.HttpMethod

data class HeaderFieldDescriptor(
        val field: String,
        val value: List<String>,
        val description: String?,
        val optional: Boolean = false
)

data class ParameterFieldDescriptor(
        val field: String,
        val value: Any,
        val description: String?,
        val type: FieldType = FieldType.OBJECT,
        val optional: Boolean = true,
        val defaultValue: Any?
)

data class BodyFieldDescriptor(
        var path: String,
        val value: Any?,
        val description: String?,
        val type: FieldType = FieldType.OBJECT,
        val optional: Boolean = false,
        val defaultValue: Any?
)

data class URIVarDescriptor(
        val field: String,
        val value: Any,
        val description: String?,
        val type: FieldType = FieldType.OBJECT
)


data class RequestProcessorDescriptor(
        val group: String = "Default",
        val name: String,
        val url: String,
        val header: List<HeaderFieldDescriptor>,
        val body: List<BodyFieldDescriptor>?,
        val parameter: List<ParameterFieldDescriptor>?,
        val method: HttpMethod,
        val uriVariables: List<URIVarDescriptor>?,
        val response: List<BodyFieldDescriptor>
)