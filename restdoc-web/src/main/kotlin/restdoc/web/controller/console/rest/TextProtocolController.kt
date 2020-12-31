package restdoc.web.controller.console.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.XmlTextDto
import restdoc.web.core.Result
import restdoc.web.core.ok
import restdoc.web.projector.XmlLinkedHashMap

@RestController
@RequestMapping("/textprotocol")
@Verify
class TextProtocolController {

    @Autowired
    lateinit var mapper: ObjectMapper

    val xmlMapper: XmlMapper = XmlMapper()

    @PostMapping("/serialize2Xml")
    fun serialize2Xml(@RequestBody param: Map<String, Any>): Result {
        return ok(xmlMapper.writeValueAsString(XmlLinkedHashMap(param)))
    }

    @PostMapping("/serialize2Json")
    fun serialize2Json(@RequestBody param: Map<String, Any>): Result {
        return ok(mapper.writeValueAsString(param))
    }

    @PostMapping("/xml2Json")
    fun xml2Json(@RequestBody dto: XmlTextDto): Result {
        return ok(mapper.writeValueAsString(xmlMapper.readValue(dto.text, XmlLinkedHashMap::class.java)))
    }


}