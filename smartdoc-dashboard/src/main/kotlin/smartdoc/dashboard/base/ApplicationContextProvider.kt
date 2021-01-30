package smartdoc.dashboard.base

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

lateinit var applicationContext: ApplicationContext


/**
 * ApplicationContextProvider
 *
 * @author Maple
 */
@Component
open class ApplicationContextProvider : ApplicationContextAware {

    override fun setApplicationContext(ac: ApplicationContext) {
        applicationContext = ac
    }
}

fun <T> getBean(type: Class<T>) = applicationContext.getBean(type)