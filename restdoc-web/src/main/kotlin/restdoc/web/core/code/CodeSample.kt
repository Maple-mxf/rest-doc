package restdoc.web.core.code

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.web.base.getBean
import restdoc.web.http.HEADER_VALUE_DELIMITER
import restdoc.web.http.HTTP1_1
import restdoc.web.model.doc.http.RestWebDocument
import restdoc.web.projector.JacksonXmlProjector
import restdoc.web.projector.JsonProjector
import restdoc.web.util.PathValue
import java.io.StringWriter
import java.util.*

/**
 * @author Maple
 * @since 1.0
 */
typealias MapToCodeSample = (RestWebDocument) -> String

internal val VE =
        {
            val ve = VelocityEngine()
            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
            ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
            ve.init()
            ve
        }()

/**
 * @author Maple
 */
@Component
open class CURLCodeSampleGenerator(private val restTemplate: RestTemplate) : MapToCodeSample {

    override fun invoke(doc: RestWebDocument): String {

        // 1 Command
        val cmd = StringBuilder("curl   -X    ${doc.method.name}")

        // 2 Header
        doc.requestHeaderDescriptor?.forEach {
            cmd.append("   -H    '${it.field}:${it.value.joinToString(separator = ",")}'")
        }

        // 3 RequestBody
        if (HttpMethod.GET != doc.method) {
            doc.requestBodyDescriptor?.let { ds ->
                val pathValues = ds.map { PathValue(it.path, it.value) }
                val json = ObjectMapper().writeValueAsString(JsonProjector(pathValues).projectToMap())
                cmd.append("   -d   '$json'")
            }
        }

        // 4 Expand URL
        val afterExpandURL: String =
                if (doc.uriVarDescriptors != null) {
                    val uriValues = doc.uriVarDescriptors!!.map { it.field to it.value }.toMap()
                    restTemplate.uriTemplateHandler.expand(doc.url, uriValues)

                            .toASCIIString()
                } else doc.url

        cmd.append("   ${afterExpandURL}")

        return cmd.toString()
    }
}

@Component
open class JavaMockCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: RestWebDocument): String {
        return ""
    }
}

@Component
open class PythonCodeSampleGenerator : MapToCodeSample {

    override fun invoke(doc: RestWebDocument): String {
        val uriFormatStr = doc.uriVarDescriptors?.joinToString(separator = ",") { "'${it.field}' = '${it.value}'" }

        val codeTemplate: Template = VE.getTemplate("codesample/PythonCodeUnitTestCaseSample.py")
        val pathValues = doc.requestBodyDescriptor?.map { PathValue(it.path, it.value) }
        val json = getBean(ObjectMapper::class.java).writeValueAsString(JsonProjector(pathValues
                ?: mutableListOf()).projectToMap())

        val ctx = VelocityContext()
        ctx.put("url", doc.url)
        ctx.put("method", doc.method)
        ctx.put("since", DateFormatUtils.format(Date(), "yyyy/MM/dd"))
        ctx.put("requestHeaders", doc.requestHeaderDescriptor)
        ctx.put("uriFormatStr", uriFormatStr)
        ctx.put("json", json)

        val writer = StringWriter()
        codeTemplate.merge(ctx, writer)

        writer.flush()
        writer.close()

        return writer.buffer.toString()
    }
}

@Component
open class JavaCodeSampleGenerator : MapToCodeSample {

    override fun invoke(doc: RestWebDocument): String {

        val codeTemplate: Template = VE.getTemplate("codesample/JavaCodeUnitTestCaseSample.java.vm")
        val ctx = VelocityContext()

        val uriVars = doc.uriVarDescriptors?.map { it.field to it.value }?.toMap()

        ctx.put("requestHeaders", doc.requestHeaderDescriptor)
        ctx.put("uriVars", uriVars)
        ctx.put("url", doc.url)
        ctx.put("method", doc.method)
        ctx.put("since", DateFormatUtils.format(Date(), "yyyy/MM/dd"))

        val writer = StringWriter()
        codeTemplate.merge(ctx, writer)

        writer.flush()
        writer.close()

        return writer.buffer.toString()
    }
}

@Component
open class KotlinCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: RestWebDocument): String {
        return ""
    }
}

/**
 * JsAjaxCodeSampleGenerator
 */
@Component
open class JsAjaxCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: RestWebDocument): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * GoCodeSampleGenerator
 */
@Component
open class GoCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: RestWebDocument): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * NodeJsCodeSampleGenerator
 */
@Component
open class NodeJsCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: RestWebDocument): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * FakeCodeSampleGenerator
 */
@Component
open class FakeCodeSampleGenerator : MapToCodeSample {
    override fun invoke(doc: RestWebDocument): String {
        val sb = StringBuilder()
        sb.append(doc.method.name).append("  ").append(doc.url).append(" ").append(HTTP1_1).append("\n\n")

        var mtp: MediaType? = null

        doc.requestHeaderDescriptor.forEach {
            sb.append(it.field).append(": ").append(it.value).append("\n")
            // TODO
            if (it.field == HttpHeaders.CONTENT_TYPE) mtp = MediaType.parseMediaType(it.value.joinToString(HEADER_VALUE_DELIMITER))
        }
        if (mtp == null) mtp = MediaType.APPLICATION_JSON
        sb.append(HttpHeaders.CONTENT_TYPE).append(" ").append(MediaType.APPLICATION_JSON_VALUE).append("\n\n")

        if (doc.method != HttpMethod.GET) {
            doc.requestBodyDescriptor.apply {

                if (MediaType.APPLICATION_JSON == mtp) {
                    val prettyString = JsonProjector(this.map { PathValue(it.path, it.value) })
                            .project()
                            .toPrettyString()

                    sb.append(prettyString).append("\n")

                } else if (MediaType.APPLICATION_XML == mtp) {
                    val prettyString = JacksonXmlProjector(this.map { PathValue(it.path, it.value) })
                            .project()

                    sb.append(prettyString).append("\n")
                }
            }
        }
        sb.append("\n")

        return sb.toString()
    }
}