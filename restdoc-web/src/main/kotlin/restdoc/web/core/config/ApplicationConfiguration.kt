package restdoc.web.core.config

import org.hibernate.validator.HibernateValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import restdoc.web.base.auth.AuthContext
import restdoc.web.base.auth.AuthMetadataImpl
import restdoc.web.base.auth.AuthenticationInterceptor
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory


/**
 * Web configuration
 * @since 1.0
 */
@Configuration
open class ApplicationConfiguration : WebMvcConfigurer {

    @Autowired
    lateinit var authContext: AuthContext

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Bean
    open fun restClient(): RestTemplate = RestTemplate()

    @Bean
    open fun authenticationInterceptor(): AuthenticationInterceptor {
        val authMetadata = AuthMetadataImpl(redisTemplate)
        val authenticationInterceptor = AuthenticationInterceptor(authContext, authMetadata)
        authenticationInterceptor.setPathPatterns(arrayOf("/**"))
        authenticationInterceptor.setOrder(0)
        authenticationInterceptor.setExcludePathPatterns(arrayOf<String>())
        return authenticationInterceptor
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        val authenticationInterceptor = authenticationInterceptor()
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns(authenticationInterceptor.pathPatterns.asList())
                .excludePathPatterns(authenticationInterceptor.excludePathPatterns.asList())
                .order(authenticationInterceptor.order)
    }


    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedOrigins("*")
    }

    @Bean
    open fun validator(): Validator? {
        val validatorFactory: ValidatorFactory = Validation.byProvider(HibernateValidator::class.java)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory()
        return validatorFactory.getValidator()
    }

    @Bean
    open fun methodValidationPostProcessor(): MethodValidationPostProcessor? {
        val postProcessor = MethodValidationPostProcessor()
        postProcessor.setValidator(validator())
        return postProcessor
    }

}