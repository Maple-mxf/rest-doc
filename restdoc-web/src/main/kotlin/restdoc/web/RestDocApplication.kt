package restdoc.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 *
 * RestDoc Springboot Application
 *
 * @since 1.0
 */
@SpringBootApplication
open class RestDocApplication

/**
 * Bootstrap main fun
 */
fun main(args: Array<String>) {
    runApplication<RestDocApplication>(*args)
}


