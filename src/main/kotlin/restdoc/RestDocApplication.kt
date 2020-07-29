package restdoc

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import restdoc.core.config.RestDocProperties

@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(value = [RestDocProperties::class])
class RestDocApplication

fun main(args: Array<String>) {
    runApplication<RestDocApplication>(*args)
}


