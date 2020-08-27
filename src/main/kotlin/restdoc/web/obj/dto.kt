package restdoc.web.obj

import javax.validation.constraints.Pattern

data class CreateProjectDto(val name: String, val desc: String?)

data class UpdateProjectDto(val id: String, val name: String, val desc: String)


data class RequestDto(
        @Pattern(regexp = "")
        var url: String,
        val method: String,
        val headers: List<HeaderDto>,
        val requestBody: List<RequestBodyDto>,
        val responseBody: List<ResponseBodyDto>,
        val executeResult: Map<String, Any>? = null)

data class HeaderDto(
        val headerKey: String,
        val headerValue: String,
        val headerDescription: String,
        val headerConstraint: Boolean
)

data class RequestBodyDto(
        val requestFieldPath: String,
        val requestFieldValue: Any,
        val requestFieldType: String,
        val requestFieldDescription: String,
        val requestFieldConstraint: Boolean
)

data class ResponseBodyDto(
        val responseFieldPath: String,
        val responseFieldValue: Any,
        val responseFieldType: String,
        val responseFieldDescription: String,
        val responseFieldConstraint: Boolean
)