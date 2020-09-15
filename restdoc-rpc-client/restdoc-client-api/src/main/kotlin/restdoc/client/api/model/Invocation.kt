package restdoc.client.api.model

import restdoc.remoting.protocol.RemotingSerializable

/**
 * Invocation
 */
interface Invocation


/**
 * DubboInvocation
 */
class DubboInvocation : Invocation, RemotingSerializable {

    lateinit var methodName: String

    lateinit var parameters: List<ObjectHolder<Any>>

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

    lateinit var queryParam: MutableMap<String, String>

    lateinit var requestBody: MutableMap<String, Any>

    lateinit var uriVariable: MutableMap<String, Any>
}