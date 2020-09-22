package restdoc.web.core.code

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import restdoc.web.model.RestWebDocument
import restdoc.web.util.JsonProjector
import restdoc.web.util.PathValue

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
                cmd.append("   -d   '${json}'")
            }
        }

        // 4 Expand URL
        val afterExpandURL: String =
                if (doc.uriVarDescriptors != null) restTemplate.uriTemplateHandler.expand(doc.url, doc.uriVarDescriptors).toASCIIString()
                else doc.url

        cmd.append("   ${afterExpandURL}")

        return cmd.toString()
    }


}

