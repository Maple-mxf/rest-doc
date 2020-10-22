package restdoc.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import restdoc.web.base.auth.Verify
import restdoc.web.core.Status
import restdoc.web.repository.RestWebDocumentRepository

@Controller
@RequestMapping("/explorer/view")
@Deprecated(message = "ExplorerViewController")
@Verify
class ExplorerViewController {

    @Autowired
    lateinit var restWebDocumentRepository: RestWebDocumentRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    @GetMapping("/{documentId}")
    fun get(@PathVariable documentId: String, model: Model): String {

        val document = restWebDocumentRepository
                .findById(documentId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError() }

        model.addAttribute("document", document)

        model.addAttribute("sample", mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(document.executeResult))

        return "explorer/detail"
    }

    @GetMapping("/{documentId}/test")
    fun command(@PathVariable documentId: String, model: Model): String {
        val document = restWebDocumentRepository
                .findById(documentId)
                .orElseThrow { Status.INVALID_REQUEST.instanceError() }

        model.addAttribute("result", mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(document.executeResult))

        return "explorer/test"
    }
}