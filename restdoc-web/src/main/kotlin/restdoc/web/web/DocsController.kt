package restdoc.web.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import restdoc.core.Result
import restdoc.core.ok
import restdoc.model.Document
import restdoc.util.JsonDeProjector

@Controller
@Deprecated(message = "")
@RequestMapping("/docs")
class DocsController {

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @GetMapping("")
    fun index(): String = "docs/add"

    @GetMapping("/json/convert")
    fun convertJSONView(): String = "docs/convert_json"

    @PostMapping("/deProjector")
    @ResponseBody
    fun deProjector(@RequestBody tree: JsonNode): Result = ok(JsonDeProjector(tree).deProject())

    @Deprecated(message = "")
    @GetMapping("/document")
    fun detail(model: Model): String {
        val document: Document? = mongoTemplate.findOne(Query(), Document::class.java)
        model.addAttribute("apiDocument", document)
        model.addAttribute("word", "Hello")
        return "docs/detail";
    }

}