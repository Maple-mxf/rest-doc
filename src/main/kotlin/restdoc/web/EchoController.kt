package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import javax.servlet.http.HttpServletRequest

@Controller
class EchoController {

    @GetMapping("/echo")
    fun index(request: HttpServletRequest): String {

        val headers = request.headerNames.toList()

        for (header in headers) {
            println("${header} : ${request.getHeader(header)}")
        }

        return "login"
    }
}