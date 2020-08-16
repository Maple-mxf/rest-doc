package restdoc.web

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
import restdoc.model.*
import java.util.*

@Controller
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

    @PostMapping("/bump")
    @ResponseBody
    fun bump(@RequestBody tree: JsonNode): Result = ok(BumpJson(tree).bump())

    @GetMapping("/document")
    fun detail(model: Model): String {
        val apiDocument: ApiDocument? = mongoTemplate.findOne(Query(), ApiDocument::class.java)
        model.addAttribute("apiDocument", apiDocument)
        model.addAttribute("word", "Hello")
        return "docs/detail";
    }

    @PostMapping("/build")
    @ResponseBody
    fun buildDoc(@RequestBody requestVo: RequestVo): Any {

        println(mapper.writeValueAsString(requestVo))

        val requestHeaderDescriptor = requestVo.headers.map {
            HeaderFieldDescriptor(
                    field = it.headerKey,
                    value = it.headerValue.split(","),
                    description = it.headerDescription,
                    optional = it.headerConstraint
            )
        }


        val requestBodyDescriptor = requestVo.requestBody.map {
            BodyFieldDescriptor(
                    path = it.requestFieldPath,
                    value = it.requestFieldValue,
                    description = it.requestFieldDescription,
                    type = FieldType.valueOf(it.requestFieldType),
                    optional = it.requestFieldConstraint,
                    defaultValue = null
            )
        }

        val apiDocument = ApiDocument(
                id = UUID.randomUUID().toString(),
                projectId = "DefaultProjectId",
                groupId = "DefaultGroupId",
                name = "DefaultName",
                resource = "DefaultResource",
                url = requestVo.url,
                requestHeaderDescriptor = requestHeaderDescriptor,
                requestParameterDescriptor = listOf(),
                requestBodyDescriptor = requestBodyDescriptor,
                uriVariables = null,
                expectResponseBody = null,
                expectResponseHeaders = null)

        mongoTemplate.save(apiDocument)

        return ok()
    }
}