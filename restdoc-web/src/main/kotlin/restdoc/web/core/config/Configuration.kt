package restdoc.web.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.validator.HibernateValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import restdoc.web.base.auth.AuthContext
import restdoc.web.base.auth.AuthenticationInterceptor
import restdoc.web.base.auth.RestDocAuthImpl
import restdoc.web.base.mongo.BaseMongoRepositoryFactoryBean
import restdoc.web.base.mongo.BaseRepositoryImpl
import restdoc.web.schedule.ScheduleProperties
import java.lang.reflect.Method
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

/**
 * @author Overman
 */
@Configuration
@AutoConfigureBefore(value = [MongoAutoConfiguration::class])
@EnableMongoRepositories(
        basePackages = ["restdoc.web.repository"],
        repositoryBaseClass = BaseRepositoryImpl::class,
        repositoryFactoryBeanClass = BaseMongoRepositoryFactoryBean::class)
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = [ScheduleProperties::class])
open class RepositoryConfiguration(private val applicationContext: ApplicationContext) {

    @Bean
    open fun mongoMappingContext(): MongoMappingContext {
        val mongoMappingContext = MongoMappingContext()
        mongoMappingContext.isAutoIndexCreation = false
        mongoMappingContext.setApplicationContext(applicationContext)
        return mongoMappingContext
    }
}

@EnableCaching
@Configuration
open class RedisConfiguration : CachingConfigurerSupport() {
    @Bean
    override fun keyGenerator(): KeyGenerator {
        return KeyGenerator { target: Any, method: Method, params: Array<Any> ->
            val sb = StringBuilder()
            sb.append(target.javaClass.name)
            sb.append(method.name)
            for (obj in params) {
                sb.append(obj.toString())
            }
            sb.toString()
        }
    }

    private inline fun <reified T> getRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, T> {
        val serializer = Jackson2JsonRedisSerializer(T::class.java)

        val objectMapper = ObjectMapper()
        serializer.setObjectMapper(objectMapper)

        val template = RedisTemplate<String, T>()
        template.connectionFactory = connectionFactory
        template.defaultSerializer = serializer
        return template
    }

    @Bean
    open fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> = getRedisTemplate(connectionFactory)
}

/**
 * ApplicationConfiguration
 */
@Configuration
open class ApplicationConfiguration : WebMvcConfigurer {

    @Autowired
    lateinit var authContext: AuthContext

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    lateinit var mapper: ObjectMapper

    @Bean
    open fun restClient(): RestTemplate = RestTemplate()


    @Bean
    open fun authenticationInterceptor(): AuthenticationInterceptor {
        val authMetadata = RestDocAuthImpl(redisTemplate, mapper)
        val authenticationInterceptor = AuthenticationInterceptor(authContext, authMetadata)
        authenticationInterceptor.pathPatterns = arrayOf("/**")
        authenticationInterceptor.order = 0
        authenticationInterceptor.excludePathPatterns = arrayOf<String>()
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
    open fun validator(): Validator {
        val validatorFactory: ValidatorFactory = Validation.byProvider(HibernateValidator::class.java)
                .configure()
                .addProperty("hibernate.validator.fail_fast", "true")
                .buildValidatorFactory()
        return validatorFactory.validator
    }

    @Bean
    open fun methodValidationPostProcessor(validator: Validator): MethodValidationPostProcessor? {
        val postProcessor = MethodValidationPostProcessor()
        postProcessor.setValidator(validator)
        return postProcessor
    }

}