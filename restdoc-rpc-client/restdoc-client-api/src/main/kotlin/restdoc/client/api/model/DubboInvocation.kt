package restdoc.client.api.model


/**
 */
data class DubboInvocation(
        val methodName: String,
        val parameters: List<ObjectHolder<Any>>,
        val refName: String,
        val returnType: String
) : Invocation

data class ObjectHolder<T>(val value: T, val className: String)