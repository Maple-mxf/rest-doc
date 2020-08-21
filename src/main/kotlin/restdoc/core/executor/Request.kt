package restdoc.core.executor

import org.springframework.http.HttpMethod

class Request {

    var url: String

    var method: HttpMethod

    var hasPathVariable: Boolean = false

    var pathVariable: Map<String, Any>

    var header: Map<String, String>

    var content: Map<String, Any>

    var queryParams: Map<String, String>

    constructor(url: String, method: HttpMethod)
            : this(url = url, method = method, hasPathVariable = false, pathVariable = mutableMapOf())

    constructor(url: String, method: HttpMethod, hasPathVariable: Boolean, pathVariable: Map<String, Any>) :
            this(url = url, method = method, hasPathVariable = hasPathVariable, pathVariable = pathVariable, header = mutableMapOf())

    constructor(url: String, method: HttpMethod, hasPathVariable: Boolean, pathVariable: Map<String, Any>,
                header: Map<String, String>) :
            this(url = url, method = method, hasPathVariable = hasPathVariable, pathVariable = pathVariable, header = header, content = mutableMapOf())


    constructor(url: String, method: HttpMethod, hasPathVariable: Boolean, pathVariable: Map<String, Any>,
                header: Map<String, String>, content: Map<String, Any>)
            :
            this(url = url, method = method, hasPathVariable = hasPathVariable, pathVariable = pathVariable, header = header, content = content,
                    queryParams = mutableMapOf())


    constructor(url: String, method: HttpMethod, hasPathVariable: Boolean, pathVariable: Map<String, Any>,
                header: Map<String, String>, content: Map<String, Any>, queryParams: Map<String, String>) {
        this.method = method
        this.hasPathVariable = hasPathVariable
        this.pathVariable = pathVariable
        this.header = header
        this.content = content
        this.queryParams = queryParams
        this.url = url
    }
}