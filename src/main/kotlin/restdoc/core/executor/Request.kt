package restdoc.core.executor

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpMethod

class Request {

    var url: String

    var method: HttpMethod

    var pathVariable: Map<String, Any>

    var header: Map<String, String>

    var content: JsonNode

    constructor(url: String, method: HttpMethod, pathVariable: Map<String, Any>,
                header: Map<String, String>, content: JsonNode) {
        this.method = method
        this.pathVariable = pathVariable
        this.header = header
        this.content = content
        this.url = url
    }
}