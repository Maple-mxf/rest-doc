package restdoc.client.dubbo.test

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import restdoc.client.dubbo.DubboAgentClientConfiguration

@EnableDubbo
@SpringBootApplication
@Import(value = [DubboAgentClientConfiguration::class])
open class DubboApplication

/**
 * Bootstrap main fun
 */
fun main(args: Array<String>) {
    runApplication<DubboApplication>(*args)
}
