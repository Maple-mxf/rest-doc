package restdoc.client.dubbo

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.stereotype.Component
import restdoc.client.api.Invoker
import restdoc.client.api.model.DubboInvocation
import restdoc.client.api.model.InvocationResult

/**
 * Dubbo invoker impl
 */
@Component
class DubboInvokerImpl(private val beanFactory: ConfigurableListableBeanFactory) : Invoker<DubboInvocation, Any> {

    override fun invoke(t: DubboInvocation): InvocationResult<Any> {
        val bean = beanFactory.getBean(t.refName)

        val paramTypes = t.parameterTypeNames
                .map { Class.forName(it.key) }
                .toTypedArray()

        val params = t.parameterTypeNames
                .map { it.value }
                .toTypedArray()

        val method = bean.javaClass.getMethod(t.methodName, *paramTypes)
        method.isAccessible = true

        val returnValue = method.invoke(bean, *params)

        return if (returnValue == null) {
            InvocationResult(
                    isNull = true,
                    returnValue = null,
                    returnValueType = method.returnType
            )
        } else {
            InvocationResult(
                    isNull = false,
                    returnValue = returnValue,
                    returnValueType = method.returnType
            )
        }
    }
}