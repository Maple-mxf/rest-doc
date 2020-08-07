package restdoc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import restdoc.core.config.RestDocProperties

/**
 *
 * RestDoc Springboot Application
 *
 * @since 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties(value = [RestDocProperties::class])
class RestDocApplication

fun main(args: Array<String>) {
    runApplication<RestDocApplication>(*args)
}


