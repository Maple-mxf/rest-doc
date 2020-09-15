package restdoc.client.dubbo

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.dubbo.common.utils.ReflectUtils
import org.springframework.stereotype.Component
import restdoc.client.api.Invoker
import restdoc.client.api.model.DubboInvocation
import restdoc.client.api.model.DubboInvocationResult
import restdoc.client.api.model.ObjectHolder

/**
 * Dubbo invoker impl
 *
 * @sample org.apache.dubbo.rpc.RpcInvocation
 */
@Component
class DubboInvokerImpl(private val beanManager: DubboRefBeanManager) : Invoker<DubboInvocation> {

    private val mapper: ObjectMapper = ObjectMapper()

    override fun rpcInvoke(t: DubboInvocation): DubboInvocationResult<Any> {
        val bean = beanManager.getRefBean(t.refName)

        val paramTypes = t.parameters.map { Class.forName(it.className) }.toTypedArray()
        val params = t.parameters.map { mappingValue(it) }.toTypedArray()

        val method = bean.javaClass.getMethod(t.methodName, *paramTypes)
        method.isAccessible = true

        val returnValue = method.invoke(bean, *params)

        return if (returnValue == null) {
            DubboInvocationResult(isSuccessful = true, exceptionMsg = null, returnValue = null, returnValueType = method.returnType)
        } else {
            DubboInvocationResult(isSuccessful = false, exceptionMsg = null, returnValue = returnValue, returnValueType = method.returnType)

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