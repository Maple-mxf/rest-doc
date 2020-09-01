package restdoc.cli

import org.springframework.boot.runApplication

open class RestDocCliApplication

fun main(args: Array<String>) {
    runApplication<RestDocCliApplication>(*args)
}