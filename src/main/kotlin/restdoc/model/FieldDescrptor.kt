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
) {

    override fun equals(other: Any?): Boolean {
        if (other is BodyFieldDescriptor) {
            return this.path.equals(other.path)
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class URIVarDescriptor(
        val field: String,
        val value: Any,
        val description: String?
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