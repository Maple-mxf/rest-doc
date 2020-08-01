package kt.spring.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/docs")
class DocsViewController {

    @GetMapping("")
    fun index(): String = "docs/add"
}