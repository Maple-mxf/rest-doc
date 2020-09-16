package restdoc.web.controller.obj

import restdoc.web.model.BodyFieldDescriptor
import restdoc.web.model.HeaderFieldDescriptor
import restdoc.web.model.ProjectType
import restdoc.web.model.URIVarDescriptor
import restdoc.web.util.FieldType
import java.net.URI
import javax.validation.constraints.NotBlank

data class CreateProjectDto(val name: String, val desc: String?, val type: ProjectType)

data class UpdateProjectDto(val id: String, val name: String, val desc: String, val type: ProjectType)

data class RequestDto(
        val projectId: String,
        var documentId: String?,
        var executeMode: String?,
        var remoteAddress: String?,
        var queryParams: MutableMap<String, Any>?,
        var url: String,
        val name: String?,
        val description: String?,
        val resource: String,
        val method: String,
        val headers: List<HeaderDto>?,
        val requestFields: List<RequestFieldsDto>?,
        val responseFields: List<ResponseFieldDto>?,
        val uriFields: List<UriVarFieldDto>?,
        val executeResult: Map<String, Any>? = null) {

    fun lookupPath(): String {
        return if (this.url.contains("http(s)?")) {
            URI(url).rawPath
        } else {
            url
        }
    }

    fun mapToHeaderDescriptor(): List<HeaderFieldDescriptor> {

        return if (!(headers == null || this.headers.isEmpty())) {
            headers
                    .filter { it.headerKey.isNotBlank() }
                    .map {
                        HeaderFieldDescriptor(
                                field = it.headerKey,
                                value = it.headerValue.split(","),
                                description = it.headerDescription,
                                optional = it.headerConstraint)
                    }
        } else mutableListOf()
    }

    fun mapToRequestDescriptor(): List<BodyFieldDescriptor> {
        return if (!(requestFields == null || this.requestFields.isEmpty())) {
            requestFields
                    .filter { it.requestFieldPath.isNotBlank() }
                    .map {
                        BodyFieldDescriptor(
                                path = it.requestFieldPath,
                                value = it.requestFieldValue,
                                description = it.requestFieldDescription,
                                type = FieldType.valueOf(it.requestFieldType.toUpperCase()),
                                optional = it.requestFieldConstraint,
                                defaultValue = null
                        )
                    }
        } else mutableListOf()
    }

    fun mapToResponseDescriptor(): List<BodyFieldDescriptor> {
        return if (!(responseFields == null || this.responseFields.isEmpty())) {
            responseFields
                    .filter { it.responseFieldPath.isNotBlank() }
                    .map {
                        BodyFieldDescriptor(
                                path = it.responseFieldPath,
                                value = it.responseFieldValue,
                                description = it.responseFieldDescription,
                                type = FieldType.valueOf(it.responseFieldType.toUpperCase()),
                                optional = it.responseFieldConstraint,
                                defaultValue = null
                        )
                    }
        } else mutableListOf()
    }

    fun mapToURIVarDescriptor(): List<URIVarDescriptor> {
        return if (this.uriFields != null && !uriFields.isEmpty()) {
            uriFields.filter { it.field != null && it.field.isNotEmpty() }
                    .map {
                        URIVarDescriptor(
                                field = it.field.toString(),
                                value = it.value.toString(),
                                description = it.desc)
                    }
        } else mutableListOf()
    }
}


data class UriVarFieldDto(
        val field: String?,
        val value: Any?,
        val desc: String?
)

data class HeaderDto(
        val headerKey: String,
        val headerValue: String,
        val headerDescription: String,
        val headerConstraint: Boolean
)

data class RequestFieldsDto(
        val requestFieldPath: String,
        val requestFieldValue: Any,
        val requestFieldType: String,
        val requestFieldDescription: String,
        val requestFieldConstraint: Boolean
)

data class ResponseFieldDto(
        val responseFieldPath: String,
        val responseFieldValue: Any,
        val responseFieldType: String,
        val responseFieldDescription: String,
        val responseFieldConstraint: Boolean
)

data class CreateResourceDto(
        val name: String,
        val tag: String,
        var pid: String
)

data class CreateUpdateWikiDto(
        var id: String?,
        @NotBlank
        val projectId: String,
        @NotBlank
        val content: String,
        @NotBlank
        val resource: String,
        @NotBlank
        val name: String,
        val tableContent: Any
)

data class UpdateNodeDto(@NotBlank(message = "名称不可为空") val name: String)

data class SyncApiEmptyTemplateDto(val clientId: String, val projectId: String, @Deprecated(message = "") val name: String)