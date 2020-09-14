package restdoc.client

import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringBootConfiguration

@SpringBootConfiguration
open class RestDocClientApplication

fun main(args: Array<String>) {
    SpringApplication.run(RestDocClientApplication::class.java)
}