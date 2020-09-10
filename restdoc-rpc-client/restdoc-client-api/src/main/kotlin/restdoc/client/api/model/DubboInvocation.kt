package restdoc.client.api.model

import restdoc.remoting.protocol.RemotingSerializable


/**
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

class ObjectHolder<T> {

    lateinit var className: String

    var value: T? = null

    constructor()

    constructor(className: String, value: T) {
        this.className = className
        this.value = value
    }

}