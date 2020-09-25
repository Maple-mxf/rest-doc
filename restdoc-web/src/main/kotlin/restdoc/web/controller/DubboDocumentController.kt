package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import restdoc.web.controller.obj.UpdateDubboDocumentDto
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.repository.DubboDocumentRepository

/**
 */
@RestController
class DubboDocumentController {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @PatchMapping("/dubboDocument/{id}")
    fun patch(@PathVariable id: String,
              @RequestBody dto: UpdateDubboDocumentDto): Result {

        val oldDocument = dubboDocumentRepository.findById(id).orElseThrow { Status.BAD_REQUEST.instanceError("id参数错误") }

        dto.description?.let {
            oldDocument.desc = it
        }

        dto.paramDescriptor?.let { descriptor ->
            val pd = oldDocument.paramDescriptors.first { it.name == descriptor.name }
            pd.description = descriptor.description
            pd.sampleValue = descriptor.sampleValue
            pd.defaultValue = descriptor.defaultValue
        }

        dto.returnValueDescriptor?.let {
            oldDocument.returnValueDescriptor.description = it.description
            oldDocument.returnValueDescriptor.sampleValue = it.sampleValue
        }

        dubboDocumentRepository.update(oldDocument)

        return ok(oldDocument)
    }
}