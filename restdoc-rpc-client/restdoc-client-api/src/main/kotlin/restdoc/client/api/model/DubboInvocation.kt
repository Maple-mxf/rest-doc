package restdoc.client.api.model

data class DubboInvocation(
        val methodName: String,
        val parameterTypeNames: LinkedHashMap<String, Any?>,
        val refName: String,
        val returnType: String
) : Invocation