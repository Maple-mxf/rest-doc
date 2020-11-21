package restdoc.web.controller.console

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import restdoc.web.controller.console.obj.SearchHeaderKeyVO
import restdoc.web.controller.console.obj.SearchHeaderValueVO
import restdoc.web.core.Result
import restdoc.web.core.ok
import java.time.Duration
import java.time.Instant
import javax.annotation.PostConstruct

/**
 * @see org.springframework.http.HttpHeaders
 */
@RequestMapping("/httpstandard/helper")
@RestController
class HttpStandardProtocolHelperController {

    private val headers: MutableList<String> = mutableListOf()

    private val optionalHeaderValues: MutableMap<String, MutableList<String>> = mutableMapOf()

    @PostConstruct
    fun init() {
        headers.add(HttpHeaders.CONTENT_TYPE)
        headers.add(HttpHeaders.ACCEPT)
        headers.add(HttpHeaders.COOKIE)
        headers.add(HttpHeaders.SET_COOKIE)
        headers.add(HttpHeaders.SET_COOKIE2)
        headers.add(HttpHeaders.ALLOW)
        headers.add(HttpHeaders.AUTHORIZATION)
        headers.add(HttpHeaders.ACCEPT_CHARSET)
        headers.add(HttpHeaders.ACCEPT_ENCODING)
        headers.add(HttpHeaders.ACCEPT_LANGUAGE)
        headers.add(HttpHeaders.ACCEPT_RANGES)
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS)
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE)
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS)

        optionalHeaderValues.putIfAbsent(HttpHeaders.CONTENT_TYPE,
                mutableListOf(
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_ATOM_XML_VALUE,
                        MediaType.APPLICATION_CBOR_VALUE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                        MediaType.APPLICATION_OCTET_STREAM_VALUE,
                        MediaType.APPLICATION_PDF_VALUE,
                        MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                        MediaType.APPLICATION_PROBLEM_XML_VALUE,
                        MediaType.APPLICATION_RSS_XML_VALUE,
                        MediaType.APPLICATION_STREAM_JSON_VALUE,
                        MediaType.APPLICATION_XHTML_XML_VALUE,
                        MediaType.APPLICATION_XML_VALUE,
                        MediaType.IMAGE_GIF_VALUE,
                        MediaType.IMAGE_JPEG_VALUE,
                        MediaType.IMAGE_PNG_VALUE,
                        MediaType.MULTIPART_FORM_DATA_VALUE,
                        MediaType.MULTIPART_MIXED_VALUE,
                        MediaType.MULTIPART_RELATED_VALUE,
                        MediaType.TEXT_EVENT_STREAM_VALUE,
                        MediaType.TEXT_MARKDOWN_VALUE,
                        MediaType.TEXT_PLAIN_VALUE,
                        MediaType.TEXT_XML_VALUE))
    }

    @GetMapping("/headerkey/search")
    fun searchStandardHeaderKey(@RequestParam(defaultValue = "") text: String): Result {
        if (text.isBlank()) return ok(headers.map { SearchHeaderKeyVO(it) })
        return ok(headers.filter { it.contains(text) }.map { SearchHeaderKeyVO(it) })
    }

    @GetMapping("/headerValue/search")
    fun searchValueByStandardHeaderKey(
            @RequestParam(defaultValue = "") headerKey: String,
            @RequestParam(defaultValue = "") text: String
    ): Result {

        return if (headerKey.isBlank()) ok(mutableListOf<SearchHeaderValueVO>())
        else {
            val values = optionalHeaderValues[headerKey]
            return if (values != null)
                if (text.isBlank()) ok(values.map { SearchHeaderValueVO(it) })
                else ok(values.filter { it.contains(text) }.map { SearchHeaderValueVO(it) })
            else ok(mutableListOf<SearchHeaderValueVO>())
        }
    }
}