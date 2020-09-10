package restdoc.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import restdoc.web.core.schedule.ScheduleProperties

/**
 *
 * RestDoc Springboot Application
 *
 * @since 1.0
 */
@SpringBootApplication
@EnableConfigurationProperties(value = [ScheduleProperties::class])
open class RestDocApplication

/**
 * Bootstrap main fun
 */
fun main(args: Array<String>) {
    runApplication<RestDocApplication>(*args)
}


