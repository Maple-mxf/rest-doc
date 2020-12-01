package restdoc.client.api.model

import com.fasterxml.jackson.annotation.JsonTypeInfo
import restdoc.remoting.protocol.RemotingSerializable

/**
 * Invocation
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
interface Invocation


/**
 * DubboInvocation
 */
class DubboInvocation : Invocation, RemotingSerializable {

    lateinit var methodName: String

    lateinit var parameters: List<ObjectHolder<Any>>

    @Deprecated(message = "refName")
    lateinit var refName: String

    lateinit var returnType: String

    constructor()

    constructor(methodName: String,
                parameters: List<ObjectHolder<Any>>,
                refName: String,
                returnType: String) {

        this.methodName = methodName
        this.parameters = parameters
        this.refName = refName
        this.returnType = returnType
    }
}


/**
 * RestWebInvocation
 */
class RestWebInvocation : Invocation, RemotingSerializable() {

    lateinit var url: String

    lateinit var method: String

    lateinit var requestHeaders: MutableMap<String, List<String>>

    lateinit var queryParam: MutableMap<String, Any?>

    lateinit var requestBody: MutableMap<String, Any>

    lateinit var uriVariable: MutableMap<String, Any>
}