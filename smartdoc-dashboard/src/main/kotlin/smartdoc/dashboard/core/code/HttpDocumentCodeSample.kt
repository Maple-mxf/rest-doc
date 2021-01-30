package smartdoc.dashboard.core.code

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
import restdoc.rpc.client.common.model.FieldType
import smartdoc.dashboard.base.getBean
import smartdoc.dashboard.http.FIXED_BOUNDARY
import smartdoc.dashboard.http.FIXED_FILE_PAIR
import smartdoc.dashboard.http.HEADER_PARAM_DELIMITER
import smartdoc.dashboard.http.HTTP1_1_VERSION
import smartdoc.dashboard.model.doc.http.HttpDocument
import smartdoc.dashboard.util.PathValue
import java.io.StringWriter
import java.util.*

/**
 * @author Maple
 * @since 1.0
 */
typealias CodeSampleMapper = (HttpDocument) -> String

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
    private val curlCodeSampleGenerator: CURLCodeSampleMapper = getBean(CURLCodeSampleMapper::class.java)
    private val javaCodeSampleGenerator: JavaCodeSampleMapper = getBean(JavaCodeSampleMapper::class.java)
    private val pythonCodeSampleGenerator: PythonCodeSampleMapper = getBean(PythonCodeSampleMapper::class.java)
    private val kotlinSampleGenerator: KotlinCodeSampleMapper = getBean(KotlinCodeSampleMapper::class.java)
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
open class CURLCodeSampleMapper(private val restTemplate: RestTemplate) : CodeSampleMapper {

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
                val json = ObjectMapper().writeValueAsString(smartdoc.dashboard.projector.JsonProjector(pathValues).projectToMap())
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

        cmd.append("   $afterExpandURL")

        return cmd.toString()
    }
}

@Component
open class PythonCodeSampleMapper : CodeSampleMapper {

    override fun invoke(doc: HttpDocument): String {
        val uriFormatStr = doc.uriVarDescriptors.joinToString(separator = ",") { "'${it.field}' = '${it.value}'" }

        val codeTemplate: Template = VE.getTemplate("codesample/PythonCodeUnitTestCaseSample.py")
        val pathValues = doc.requestBodyDescriptor.map { PathValue(it.path, it.value) }
        val json = getBean(ObjectMapper::class.java).writeValueAsString(smartdoc.dashboard.projector.JsonProjector(pathValues
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
open class JavaCodeSampleMapper : CodeSampleMapper {

    override fun invoke(doc: HttpDocument): String {

        val codeTemplate: Template = VE.getTemplate("codesample/JavaCodeUnitTestCaseSample.java.vm")
        val ctx = VelocityContext()

        val uriVars = doc.uriVarDescriptors.map { it.field to it.value }.toMap()

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
open class KotlinCodeSampleMapper : CodeSampleMapper {
    override fun invoke(p1: HttpDocument): String {
        return ""
    }
}

/**
 * JsAjaxCodeSampleGenerator
 */
@Component
open class JsAjaxCodeSampleMapper : CodeSampleMapper {
    override fun invoke(p1: HttpDocument): String {
        TODO("not implemented")
    }
}

/**
 * GoCodeSampleGenerator
 */
@Component
open class GoCodeSampleMapper : CodeSampleMapper {
    override fun invoke(p1: HttpDocument): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * NodeJsCodeSampleGenerator
 */
@Component
open class NodeJsCodeSampleGenerator : CodeSampleMapper {
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
open class RequestFakeCodeSampleGenerator : CodeSampleMapper {
    override fun invoke(doc: HttpDocument): String {
        val sb = StringBuilder()

        var url = doc.url

        // Matrix Vars
        for (matrixVariableDescriptor in doc.matrixVariableDescriptors) {
            val value = if (matrixVariableDescriptor.defaultValue == null || ValueConstants.DEFAULT_NONE == matrixVariableDescriptor.defaultValue) {
                "{${matrixVariableDescriptor.field}}"
            } else {
                matrixVariableDescriptor.defaultValue
            }

            url = if (matrixVariableDescriptor.pathVar != null) {
                url.replace("{${matrixVariableDescriptor.pathVar}}", "" +
                        "{${matrixVariableDescriptor.pathVar}};${matrixVariableDescriptor.field}=$value")
            } else {
                "$url;${matrixVariableDescriptor.field}=$value"
            }
        }

        sb.append(doc.method.name).append("  ").append(url)

        if (doc.queryParamDescriptors.isNotEmpty()) {
            val queryString = doc.queryParamDescriptors
                    .joinToString(separator = "&") {
                        if (it.value == ValueConstants.DEFAULT_NONE) "${it.field}={${it.field}}"
                        else "${it.field}=${it.value}"
                    }
            sb.append("?$queryString").append("  ")
        }else{
            sb.append("  ")
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

                when (mtp) {
                    MediaType.APPLICATION_JSON_VALUE -> {
                        val prettyString = smartdoc.dashboard.projector.JsonProjector(this.map { PathValue(it.path, it.value) })
                                .project()
                                .toPrettyString()

                        sb.append(prettyString).append("\n")

                    }
                    MediaType.APPLICATION_XML_VALUE -> {
                        val prettyString = smartdoc.dashboard.projector.JacksonXmlProjector(this.map { PathValue(it.path, it.value) })
                                .project()

                        sb.append(prettyString).append("\n")
                    }

                    MediaType.MULTIPART_FORM_DATA_VALUE -> {
                        for (item in this) {
                            sb.append(FIXED_BOUNDARY).append("\n")
                            sb.append("Content-Disposition: form-data").append(HEADER_PARAM_DELIMITER)
                            sb.append("name=").append(item.path)
                            when (item.type) {
                                // Content-Disposition: form-data; name="myFile"; filename="foo.txt"
                                FieldType.FILE -> {
                                    sb.append(HEADER_PARAM_DELIMITER).append(FIXED_FILE_PAIR)
                                    sb.append("file content....")
                                }
                                // Content-Disposition: form-data; name="description"
                                else -> {
                                    sb.append(item.value)
                                }
                            }
                            sb.append(FIXED_BOUNDARY).append("\n")
                        }
                    }

                    else -> {
                        val prettyString = smartdoc.dashboard.projector.JsonProjector(this.map { PathValue(it.path, it.value) })
                                .project()
                                .toPrettyString()

                        sb.append(prettyString).append("\n")
                    }
                }
            }
        }
        return sb.toString()
    }
}

@Component
open class ResponseFakeCodeSampleGenerator : CodeSampleMapper {
    override fun invoke(doc: HttpDocument): String {
        val sb = StringBuilder()

        if (doc.responseBodyDescriptors.isNotEmpty()) {
            val prettyString = smartdoc.dashboard.projector.JsonProjector(doc.responseBodyDescriptors.map { PathValue(it.path, it.value) })
                    .project()
                    .toPrettyString()

            sb.append(prettyString).append("\n")
        } else {
            sb.append("无响应示例")
        }

        return sb.toString()
    }

}