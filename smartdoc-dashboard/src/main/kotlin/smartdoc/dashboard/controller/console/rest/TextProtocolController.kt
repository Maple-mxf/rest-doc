package smartdoc.dashboard.controller.console.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import smartdoc.dashboard.controller.console.model.XmlTextDto

import smartdoc.dashboard.core.Result
import smartdoc.dashboard.core.ok

@RestController
@RequestMapping("/textprotocol")
@smartdoc.dashboard.base.auth.Verify
class TextProtocolController {

    @Autowired
    lateinit var mapper: ObjectMapper

    val xmlMapper: XmlMapper = XmlMapper()

    @PostMapping("/serialize2Xml")
    fun serialize2Xml(@RequestBody param: Map<String, Any>): Result {
        return ok(xmlMapper.writeValueAsString(smartdoc.dashboard.projector.XmlLinkedHashMap(param)))
    }

    @PostMapping("/serialize2Json")
    fun serialize2Json(@RequestBody param: Map<String, Any>): Result {
        return ok(mapper.writeValueAsString(param))
    }

    @PostMapping("/xml2Json")
    fun xml2Json(@RequestBody dto: XmlTextDto): Result {
        return ok(mapper.writeValueAsString(xmlMapper.readValue(dto.text, smartdoc.dashboard.projector.XmlLinkedHashMap::class.java)))
    }


}