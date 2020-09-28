package restdoc.web.core.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.hibernate.validator.HibernateValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.mapping.MongoMappingContext
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import restdoc.web.base.auth.AuthContext
import restdoc.web.base.auth.AuthMetadataImpl
import restdoc.web.base.auth.AuthenticationInterceptor
import restdoc.web.base.mongo.BaseMongoRepositoryFactoryBean
import restdoc.web.base.mongo.BaseRepositoryImpl
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
open class RepositoryConfiguration(private val applicationContext: ApplicationContext) {

    @Bean open fun mongoMappingContext(): MongoMappingContext {
        val mongoMappingContext = MongoMappingContext()
        mongoMappingContext.isAutoIndexCreation = false
        mongoMappingContext.setApplicationContext(applicationContext)
        return mongoMappingContext
    }
}


@Configuration
@EnableCaching
open class RedisConfiguration : CachingConfigurerSupport() {
    @Bean override fun keyGenerator(): KeyGenerator {
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

    @Bean open fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> { // 设置序列化
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        //
        val om = ObjectMapper()
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        //        om.enableDefaultTyping(DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om)
        // 配置redisTemplate
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory
        val stringSerializer: RedisSerializer<*> = StringRedisSerializer()
        // key序列化
        redisTemplate.keySerializer = stringSerializer
        // value序列化
        redisTemplate.valueSerializer = jackson2JsonRedisSerializer
        // Hash key序列化
        redisTemplate.hashKeySerializer = stringSerializer
        // Hash value序列化
        redisTemplate.hashValueSerializer = jackson2JsonRedisSerializer
        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }
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

    @Bean
    open fun restClient(): RestTemplate = RestTemplate()


    @Bean
    open fun authenticationInterceptor(): AuthenticationInterceptor {
        val authMetadata = AuthMetadataImpl(redisTemplate)
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