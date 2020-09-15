package restdoc.client.restweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RestDocClientApplication

fun main(args: Array<String>) {
    runApplication<RestDocClientApplication>(*args)
}