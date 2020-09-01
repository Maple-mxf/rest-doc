package restdoc.common

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RestDocCommonApplication

fun main(args: Array<String>) {
    runApplication<RestDocCommonApplication>(*args)
}