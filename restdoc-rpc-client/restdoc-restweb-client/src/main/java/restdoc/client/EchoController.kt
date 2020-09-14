package restdoc.client

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class EchoController {
    @GetMapping(value = ["/echo/{var}"], produces = [MediaType.APPLICATION_JSON_VALUE]) fun echo(@PathVariable `var`: String?
    ): Any {
        System.err.println(`var`)
        val map = HashMap<Any, Any>()
        map["serviceName"] = "restdoc-starter"
        map["time"] = Date().time
        map["success"] = true
        map["system"] = "windows"
        map["tcp model"] = "nio channel"
        map["tcp framework"] = "netty-4.0.Final"
        return map
    }
}