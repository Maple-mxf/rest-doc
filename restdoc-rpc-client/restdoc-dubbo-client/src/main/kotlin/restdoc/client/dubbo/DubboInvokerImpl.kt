package restdoc.client.dubbo

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.dubbo.common.utils.ReflectUtils
import org.springframework.stereotype.Component
import restdoc.client.api.Invoker
import restdoc.client.api.model.DubboInvocation
import restdoc.client.api.model.InvocationResult
import restdoc.client.api.model.ObjectHolder

/**
 * Dubbo invoker impl
 *
 * @sample org.apache.dubbo.rpc.RpcInvocation
 */
@Component
class DubboInvokerImpl(private val beanManager: DubboRefBeanManager) : Invoker<DubboInvocation, Any> {

    private val mapper: ObjectMapper = ObjectMapper()

    override fun invoke(t: DubboInvocation): InvocationResult<Any> {
        val bean = beanManager.getRefBean(t.refName)

        val paramTypes = t.parameters.map { Class.forName(it.className) }.toTypedArray()
        val params = t.parameters.map { mappingValue(it) }.toTypedArray()

        val method = bean.javaClass.getMethod(t.methodName, *paramTypes)
        method.isAccessible = true

        val returnValue = method.invoke(bean, *params)

        return if (returnValue == null) {
            InvocationResult(isNull = true, returnValue = null, returnValueType = method.returnType)
        } else {
            InvocationResult(isNull = false, returnValue = returnValue, returnValueType = method.returnType)
        }
    }

    private fun mappingValue(holder: ObjectHolder<Any>): Any? {
        val type = ReflectUtils.forName(holder.className)
        if (ReflectUtils.isPrimitive(type)) {
            return holder.value
        } else {
            return mapper.convertValue(holder.value, type)
        }
    }
}