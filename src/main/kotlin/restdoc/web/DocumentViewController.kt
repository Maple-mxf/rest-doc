package restdoc.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class DocumentViewController {

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @GetMapping("/{projectId}/document/view/list/")
    fun list(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/list"
    }

    @GetMapping("/{id}")
    fun get(): String {
        return ""
    }

    @GetMapping("/document/view/executeResult")
    fun executeResult(): String = "docs/executeResult"


    @GetMapping("/document/view/httpTask/{taskId}")
    fun execute(@PathVariable taskId: String, model: Model): String {
        model.addAttribute("taskId", taskId)
        return "docs/executeResult"
    }

    @GetMapping("/document/view/desc")
    fun desc(): String {
        return "docs/desc"
    }

    @GetMapping("/{projectId}/document/view/wiki/add")
    fun createWiki(@PathVariable projectId: String, model: Model): String {
        model.set("projectId", projectId)
        return "docs/addWiki"
    }
}