package smartdoc.dashboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 *
 * RestDoc Springboot Application
 *
 * @since 1.0
 */
@SpringBootApplication
open class SmartDocApplication

/**
 * Bootstrap main fun
 */
fun main(args: Array<String>) {
    runApplication<SmartDocApplication>(*args)
}


