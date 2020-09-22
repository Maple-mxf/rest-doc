package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import restdoc.web.repository.DubboDocumentRepository

/**
 * @sample restdoc.web.model.RestWebDocument
 * @sample restdoc.web.model.DubboDocument
 */
@RestController
class DubboDocumentController {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

}