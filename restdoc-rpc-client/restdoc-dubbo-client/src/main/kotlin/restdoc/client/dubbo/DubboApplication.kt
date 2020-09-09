package restdoc.client.dubbo

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableDubbo
@SpringBootApplication
open class DubboApplication

/**
 * Bootstrap main fun
 */
fun main(args: Array<String>) {
    runApplication<DubboApplication>(*args)
}
