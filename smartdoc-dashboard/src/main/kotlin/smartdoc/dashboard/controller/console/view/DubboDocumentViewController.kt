package smartdoc.dashboard.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.repository.DubboDocumentRepository
import smartdoc.dashboard.repository.ResourceRepository

/**
 */
@Controller
@smartdoc.dashboard.base.auth.Verify
class DubboDocumentViewController {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @Autowired
    lateinit var resourceRepository: ResourceRepository

    @GetMapping("/{resourceId}/dubboDocument")
    fun getDocsByResources(@PathVariable resourceId: String, model: Model): String {

        val resource = resourceRepository.findById(resourceId).orElseThrow(Status.BAD_REQUEST::instanceError)

        val query = Query().addCriteria(Criteria("resource").`is`(resourceId))
        val docs = dubboDocumentRepository.list(query)

        docs.forEach {
            if (it.desc.isBlank()) it.desc = "此处填写备注"
        }

        model.addAttribute("docs", docs)

        model.addAttribute("resourceName", resource.name)

        return "docs/dubbo_doc_detail"
    }


    @Suppress("ThrowableNotThrown")
    @GetMapping("/dubboDocument/{documentId}/snippet/view")
    @Throws(exceptionClasses = [Throwable::class])
    fun editInParamPage(@PathVariable documentId: String,
                        @RequestParam(required = false, defaultValue = "") paramName: String,
                        @RequestParam type: String,
                        model: Model): String {

        val document =
                dubboDocumentRepository.findById(documentId).orElseThrow { Status.BAD_REQUEST.instanceError("documentId参数错误") }

        return if (type == "in") {
            model.addAttribute("descriptor", document.paramDescriptors.first { it.name == paramName })
            "docs/edit_inparam"
        } else {
            model.addAttribute("descriptor", document.returnValueDescriptor)
            "docs/edit_outparam"
        }
    }

    @Suppress("ThrowableNotThrown")
    @GetMapping("/dubboDocument/{documentId}/param/fill/view")
    fun fillInParamPage(@PathVariable documentId: String, model: Model): String {
        val document =
                dubboDocumentRepository.findById(documentId).orElseThrow { Status.BAD_REQUEST.instanceError("documentId参数错误") }

        model.addAttribute("paramDescriptors", document.paramDescriptors)

        return "docs/fill_inparam"
    }

    @GetMapping("/dubboDocument/{documentId}/description/view")
    fun editDescriptionPage(@PathVariable documentId: String, model: Model): String{
        val document =
                dubboDocumentRepository.findById(documentId).orElseThrow { Status.BAD_REQUEST.instanceError("documentId参数错误") }
        model.addAttribute("field", document.desc)
        return "docs/edit_description"
    }

}