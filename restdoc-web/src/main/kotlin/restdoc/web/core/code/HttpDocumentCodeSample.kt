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
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.client.RestTemplate
import restdoc.web.base.getBean
import restdoc.web.http.HTTP1_1_VERSION
import restdoc.web.model.doc.http.HttpDocument
import restdoc.web.projector.JacksonXmlProjector
import restdoc.web.projector.JsonProjector
import restdoc.web.util.PathValue
import java.io.StringWriter
import java.util.*

/**
 * @author Maple
 * @since 1.0
 */
typealias MapToCodeSample = (HttpDocument) -> String

internal val VE =
        {
            val ve = VelocityEngine()
            ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
            ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name)
            ve.init()
            ve
        }()

interface CodeSample {
    fun kotlinCode(): String
    fun javaCode(): String
    fun pythonCode(): String
    fun curlCode(): String
    fun fakeRequestCode(): String
    fun fakeResponseCode(): String
}

/**
 *
 * CodeSampleImpl
 */
open class CodeSampleImpl(val doc: HttpDocument) : CodeSample {
    private val curlCodeSampleGenerator: CURLCodeSampleGenerator = getBean(CURLCodeSampleGenerator::class.java)
    private val javaCodeSampleGenerator: JavaCodeSampleGenerator = getBean(JavaCodeSampleGenerator::class.java)
    private val pythonCodeSampleGenerator: PythonCodeSampleGenerator = getBean(PythonCodeSampleGenerator::class.java)
    private val kotlinSampleGenerator: KotlinCodeSampleGenerator = getBean(KotlinCodeSampleGenerator::class.java)
    private val requestFakeCodeSampleGenerator: RequestFakeCodeSampleGenerator = getBean(RequestFakeCodeSampleGenerator::class.java)
    private val responseFakeCodeSampleGenerator: ResponseFakeCodeSampleGenerator = getBean(ResponseFakeCodeSampleGenerator::class.java)

    val codeMap: MutableMap<String, String> = mutableMapOf()

    init {
        codeMap["java"] = javaCodeSampleGenerator.invoke(doc)
        codeMap["python"] = pythonCodeSampleGenerator.invoke(doc)
        codeMap["kotlin"] = kotlinSampleGenerator.invoke(doc)
        codeMap["curl"] = curlCodeSampleGenerator.invoke(doc)
        codeMap["requestFake"] = requestFakeCodeSampleGenerator.invoke(doc)
        codeMap["responseFake"] = responseFakeCodeSampleGenerator.invoke(doc)
    }

    override fun kotlinCode(): String = codeMap["kotlin"]!!

    override fun javaCode(): String = codeMap["java"]!!

    override fun pythonCode(): String = codeMap["python"]!!

    override fun curlCode(): String = codeMap["curl"]!!

    override fun fakeRequestCode(): String = codeMap["requestFake"]!!

    override fun fakeResponseCode(): String = codeMap["responseFake"]!!
}


/**
 * @author Maple
 */
@Component
open class CURLCodeSampleGenerator(private val restTemplate: RestTemplate) : MapToCodeSample {

    override fun invoke(doc: HttpDocument): String {

        // 1 Command
        val cmd = StringBuilder("curl   -X    ${doc.method.name}")

        // 2 Header
        doc.requestHeaderDescriptor.forEach {
            cmd.append("   -H    '${it.field}:${it.value}'")
        }

        // 3 RequestBody
        if (HttpMethod.GET != doc.method) {
            doc.requestBodyDescriptor.let { ds ->
                val pathValues = ds.map { PathValue(it.path, it.value) }
                val json = ObjectMapper().writeValueAsString(JsonProjector(pathValues).projectToMap())
                cmd.append("   -d   '$json'")
            }
        }

        // 4 Expand URL
        val afterExpandURL: String =
                if (doc.uriVarDescriptors.isNotEmpty()) {
                    val uriValues = doc.uriVarDescriptors.map { it.field to it.value }.toMap()
                    restTemplate.uriTemplateHandler.expand(doc.url, uriValues)

                            .toASCIIString()
                } else doc.url

        cmd.append("   ${afterExpandURL}")

        return cmd.toString()
    }
}

@Component
open class JavaMockCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: HttpDocument): String {
        return ""
    }
}

@Component
open class PythonCodeSampleGenerator : MapToCodeSample {

    override fun invoke(doc: HttpDocument): String {
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

    override fun invoke(doc: HttpDocument): String {

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
    override fun invoke(p1: HttpDocument): String {
        return ""
    }
}

/**
 * JsAjaxCodeSampleGenerator
 */
@Component
open class JsAjaxCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: HttpDocument): String {
        TODO("not implemented")
    }
}

/**
 * GoCodeSampleGenerator
 */
@Component
open class GoCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: HttpDocument): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * NodeJsCodeSampleGenerator
 */
@Component
open class NodeJsCodeSampleGenerator : MapToCodeSample {
    override fun invoke(p1: HttpDocument): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * FakeCodeSampleGenerator
 *
 * https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Resources_and_specifications
 */
@Component
open class RequestFakeCodeSampleGenerator : MapToCodeSample {
    override fun invoke(doc: HttpDocument): String {
        val sb = StringBuilder()

        // Matrix Vars
        if (doc.matrixVariableDescriptors.isNotEmpty()) {

        }

        sb.append(doc.method.name).append("  ").append(doc.url)

        if (doc.queryParamDescriptors.isNotEmpty()) {
            val queryString = doc.queryParamDescriptors
                    .joinToString(separator = "&") {
                        if (it.value == ValueConstants.DEFAULT_NONE) "${it.field}={${it.field}}"
                        else "${it.field}=${it.value}"
                    }
            sb.append("?$queryString").append("  ")
        }

        sb.append(HTTP1_1_VERSION).append("\n")


        val headers = doc.requestHeaderDescriptor.map { t -> t.field to t.value }.toMap().toMutableMap()
        var mtp: String? = headers[HttpHeaders.CONTENT_TYPE]

        if (mtp == null) {
            mtp = if (doc.method == HttpMethod.GET)
                MediaType.TEXT_HTML.toString()
            else MediaType.ALL.toString()
        }

        headers.forEach { t ->
            sb.append(t.key).append(": ").append(t.value).append("\n")
        }
        sb.append("\n")
        if (doc.method == HttpMethod.GET) {
            // TODO
        } else {
            doc.requestBodyDescriptor.apply {

                if (MediaType.APPLICATION_JSON_VALUE == mtp) {
                    val prettyString = JsonProjector(this.map { PathValue(it.path, it.value) })
                            .project()
                            .toPrettyString()

                    sb.append(prettyString).append("\n")

                } else if (MediaType.APPLICATION_XML_VALUE == mtp) {
                    val prettyString = JacksonXmlProjector(this.map { PathValue(it.path, it.value) })
                            .project()

                    sb.append(prettyString).append("\n")
                } else {
                    val prettyString = JsonProjector(this.map { PathValue(it.path, it.value) })
                            .project()
                            .toPrettyString()

                    sb.append(prettyString).append("\n")
                }
            }
        }
        return sb.toString()
    }
}

@Component
open class ResponseFakeCodeSampleGenerator : MapToCodeSample {
    override fun invoke(doc: HttpDocument): String {
        val sb = StringBuilder()

        if (doc.responseBodyDescriptors.isNotEmpty()) {
            val prettyString = JsonProjector(doc.responseBodyDescriptors.map { PathValue(it.path, it.value) })
                    .project()
                    .toPrettyString()

            sb.append(prettyString).append("\n")
        }

        return sb.toString()
    }

}