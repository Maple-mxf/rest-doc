package restdoc.web.core.code

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.velocity.Template
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.web.base.getBean
import restdoc.web.model.RestWebDocument
import restdoc.web.util.JsonProjector
import restdoc.web.util.PathValue
import java.io.StringWriter
import java.util.*

typealias MapToCodeSample = (RestWebDocument) -> String


/**
 * @author Overman
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
                if (doc.uriVarDescriptors != null)
                {
                    val uriValues = doc.uriVarDescriptors!!.map { it.field to it.value }.toMap()
                    restTemplate.uriTemplateHandler.expand(doc.url, uriValues)

                            .toASCIIString()
                }
                else doc.url

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
    private val ve = VelocityEngine()

    init {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name);
        ve.init();
    }

    override fun invoke(doc: RestWebDocument): String {
        val uriFormatStr = doc.uriVarDescriptors?.joinToString(separator = ",") { "'${it.field}' = '${it.value}'" }

        val codeTemplate: Template = ve.getTemplate("codesample/PythonCodeUnitTestCaseSample.py")
        val pathValues = doc.requestBodyDescriptor?.map { PathValue(it.path, it.value) }
        val json = getBean(ObjectMapper::class.java).writeValueAsString(JsonProjector(pathValues
                ?: mutableListOf()).projectToMap())

        val ctx = VelocityContext()
        ctx.put("url", doc.url)
        ctx.put("method", doc.method)
        ctx.put("since", DateFormatUtils.format(Date(), "yyyy/MM/dd"))
        ctx.put("requestHeaders", doc.requestHeaderDescriptor)
        ctx.put("uriFormatStr",uriFormatStr)
        ctx.put("json",json)

        val writer = StringWriter()
        codeTemplate.merge(ctx, writer)

        writer.flush()
        writer.close()

        return writer.buffer.toString()
    }
}

@Component
open class JavaCodeSampleGenerator : MapToCodeSample {

    private val ve = VelocityEngine()

    init {
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath")
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader::class.java.name);
        ve.init();
    }

    override fun invoke(doc: RestWebDocument): String {

        val codeTemplate: Template = ve.getTemplate("codesample/JavaCodeUnitTestCaseSample.java.vm")
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