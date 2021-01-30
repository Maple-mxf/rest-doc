package smartdoc.dashboard.http

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import smartdoc.dashboard.base.getBean


@Deprecated(message = "ContentTypeAdaptive")
internal val contentTypeAdaptive: ContentTypeAdaptive = getBean(ContentTypeAdaptive::class.java)

internal var cookieAdaptive: CookieAdaptive = getBean(CookieAdaptive::class.java)

fun parseHeader(method: HttpMethod, bodyRequired: Boolean, fileRequired: Boolean, headers: Map<String, List<String>>): Map<String, String> {

    val headerMap: MutableMap<String, String> = mutableMapOf()

    for (headerEntry in headers) {

        if (HttpHeaders.CONTENT_TYPE == headerEntry.key) {
            headerMap[HttpHeaders.CONTENT_TYPE] =
                    contentTypeAdaptive.invoke(method, bodyRequired, fileRequired, headerEntry.value)
        } else if (HttpHeaders.COOKIE == headerEntry.key ||
                HttpHeaders.SET_COOKIE == headerEntry.key) {

            headerMap[headerEntry.key] =
                    cookieAdaptive.invoke(method, bodyRequired, fileRequired, headerEntry.value)
        }
    }
    return headerMap
}

typealias HttpRequestHeaderAdaptive = (method: HttpMethod, bodyRequired: Boolean, fileRequired: Boolean, values: List<String>) -> String

/**
 * https://tools.ietf.org/html/rfc7231#section-3.1.1.5
 *
 * The "Content-Type" header field indicates the media type of the
 * associated representation: either the representation enclosed in the
 * message payload or the selected representation, as determined by the
 * message semantics.  The indicated media type defines both the data
 * format and how that data is intended to be processed by a recipient,
 * within the scope of the received message semantics, after any content
 * codings indicated by Content-Encoding are decoded.
 */
@Component
open class ContentTypeAdaptive : HttpRequestHeaderAdaptive {

    // Multipurpose Internet Mail Extensions | Mime

    override fun invoke(method: HttpMethod, bodyRequired: Boolean, fileRequired: Boolean, values: List<String>): String {
        if (values.isEmpty()) return ""

        // MultiPart
        // Multiple-resource bodies，由多部分 body 组成，每一部分包含不同的信息位。通常是和  HTML Forms 连系在一起。
        if (fileRequired) return "multipart/form-data; $FIXED_BOUNDARY"

        val mts = values
                .mapNotNull {
                    try {
                        MediaType.parseMediaType(it)
                    } catch (ignored: Exception) {
                        null
                    }
                }

        val concreteMt = mts.find { it.isConcrete }

        if (concreteMt != null) {
            return concreteMt.toString()
        }

        return if (bodyRequired) {
            if (method != HttpMethod.GET) {
                if (fileRequired) {
                    "multipart/form-data; boundary=$FIXED_BOUNDARY"
                } else {
                    MediaType.APPLICATION_JSON_VALUE
                }
            } else {
                MediaType.ALL.toString()
            }
        } else {
            MediaType.ALL.toString()
        }
    }
}

/**
 * https://tools.ietf.org/html/rfc6265#section-5.4
 * Cookie: <cookie-list>
 */
@Component
open class CookieAdaptive : HttpRequestHeaderAdaptive {
    override fun invoke(method: HttpMethod, bodyRequired: Boolean, fileRequired: Boolean, values: List<String>): String {
        if (values.isEmpty()) return ""
        return values.joinToString(HEADER_PARAM_DELIMITER)
    }
}

/**
 *
 */
@Component
open class AcceptAdaptive : HttpRequestHeaderAdaptive {
    override fun invoke(method: HttpMethod, bodyRequired: Boolean, fileRequired: Boolean, values: List<String>): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

