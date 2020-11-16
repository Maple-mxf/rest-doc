package restdoc.web.controller.console

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import restdoc.web.base.auth.Verify
import restdoc.web.util.TemplateUtil
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@Verify
class EchoController {

    @GetMapping("/echo")
    fun index(request: HttpServletRequest): String {
        val headers = request.headerNames.toList()
        for (header in headers) {
            println("${header} : ${request.getHeader(header)}")
        }
        return "login"
    }

    @GetMapping("/render")
    @ResponseBody
    fun render(response: HttpServletResponse,request: HttpServletRequest): String {

        val headers = request.headerNames.toList()
        for (header in headers) {
            println("${header} : ${request.getHeader(header)}")
        }

        val keywordLine = "<a href='https://www.baidu.com'>SEO关键字<a>"
        return TemplateUtil.gen(mutableMapOf<String, Any>("keywordLine" to keywordLine))
    }
}