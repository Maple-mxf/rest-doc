package restdoc.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web configuration
 * @since 1.0
 */
@Configuration
open class WebConfiguration : WebMvcConfigurer {

    /**
     * Create restTemplate bean
     * @return rest Client
     */
    @Bean
    open fun restClient(): RestTemplate = RestTemplate()

}