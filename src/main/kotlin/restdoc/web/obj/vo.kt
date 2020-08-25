package restdoc.web.obj

import javax.validation.constraints.Pattern

data class RequestVo(
        @Pattern(regexp = "")
        val url: String,

        val method: String,
        val headers: List<HeaderVo>,
        val requestBody: List<RequestBodyVo>,
        val responseBody: List<ResponseBodyVo>)

data class HeaderVo(
        val headerKey: String,
        val headerValue: String,
        val headerDescription: String,
        val headerConstraint: Boolean
)

data class RequestBodyVo(
        val requestFieldPath: String,
        val requestFieldValue: Any,
        val requestFieldType: String,
        val requestFieldDescription: String,
        val requestFieldConstraint: Boolean
)

data class ResponseBodyVo(
        val responseFieldPath: String,
        val responseFieldValue: Any,
        val responseFieldType: String,
        val responseFieldDescription: String,
        val responseFieldConstraint: Boolean
)