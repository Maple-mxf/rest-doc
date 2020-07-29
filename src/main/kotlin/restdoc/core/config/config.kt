package restdoc.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

//@EnableWebMvc
@Configuration
class WebConfiguration:WebMvcConfigurer {

    @Bean
    fun restClient(): RestTemplate = RestTemplate()

//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("/WEB-INF/static/");
//    }
}