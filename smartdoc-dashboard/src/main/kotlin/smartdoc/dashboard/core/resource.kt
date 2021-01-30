package smartdoc.dashboard.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component


/**
 * @sample AsciidoctorTemplateResource
 * @since 1.0
 */
interface TemplateResource {

    fun apiName(): Resource

    fun apiDescription(): Resource

    fun apiVersion(): Resource

    fun curlRequest(): Resource

    fun httpHeader(): Resource

    fun requestField(): Resource

    fun requestFieldDescription(): Resource

    fun responseField(): Resource

    fun responseFieldDescription(): Resource

    fun requestProcessSample(): Resource
}

@Component
class AsciidoctorTemplateResource(@Autowired val resourceLoader: ResourceLoader) : TemplateResource {

    override fun apiName(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-api-name.snippet")
    }

    override fun apiDescription(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-api-descriotion.snippet")
    }

    override fun apiVersion(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-api-version.snippet")
    }

    override fun curlRequest(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-curl-request.snippet")
    }

    override fun httpHeader(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-request-headers.snippet")
    }

    override fun requestField(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-response-fields.snippet")
    }

    override fun requestFieldDescription(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-request-field-desc.snippet")
    }

    override fun responseField(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/ddefault-response-body.snippet")
    }

    override fun responseFieldDescription(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-response-field-desc.snippet")
    }

    override fun requestProcessSample(): Resource {
        return resourceLoader.getResource("classpath:restdic/restdoc.web.core/template/asciidoctor/default-request-process-sample.snippet")
    }
}

