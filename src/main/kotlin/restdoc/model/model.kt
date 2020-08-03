package restdoc.model

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.http.HttpMethod

@Document(collation = "restdoc_api_project_config")
data class ApiProjectConfig(
        @Id var id: String?,
        val projectId: String,
        val testURIPrefix: String
);


@Document(collation = "restdoc_api_project")
data class ApiProject(
        @Id val id: String,
        val name: String,
        val desc: String,
        val createTime: Long
)

@Document(collation = "restdoc_api_group")
data class ApiGroup(
        @Id val id: String,
        val name: String,
        val desc: String,
        val createTime: Long
)

//@Document(collation = "restdoc_api_doc")
data class ApiDocument(
        @Id var id: String?,
        var projectId: String?,
        var groupId: String?,
        var name: String?,
        var resource: String?,
        val url: String,
        var requestHeaderDescriptor: List<HeaderFieldDescriptor>?,
        var requestParameterDescriptor: List<ParameterDescriptor>?,
        var requestBodyDescriptor: List<BodyFieldDescriptor>?,
        val method: HttpMethod = HttpMethod.GET,
        val uriVariables: List<URIVarDescriptor>?,
        val expectResponseHeaders: Any?,
        val expectResponseBody: JsonNode?
)


data class Menu(

        val id: Int,
        val title: String,
        val type: Int,
        val openType: String? = "_iframe",
        val icon: String,
        val href: String = "",
        val children: MutableList<Menu>? = null
)