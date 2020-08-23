package restdoc.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/project/view")
@Controller
class ProjectViewController {

    @GetMapping("")
    fun list(): String {
        return "project/list"
    }

    @GetMapping("/{id}")
    fun get(): String {
        return "forward:project/detail";
    }

    @GetMapping("/add")
    fun create(): String = "project/add"
}