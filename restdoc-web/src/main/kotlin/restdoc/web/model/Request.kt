package restdoc.model

import org.springframework.http.HttpMethod

data class Request(
        val url: String,
        val method: HttpMethod = HttpMethod.GET,
        val header: MutableMap<String, Any>?,
        val body: Any?,
        val uriVar: Map<String, Any>?
)

//data class Response()