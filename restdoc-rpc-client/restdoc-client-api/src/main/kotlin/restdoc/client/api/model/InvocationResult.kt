package restdoc.client.api.model

import restdoc.remoting.protocol.RemotingSerializable

/**
 * InvocationResult
 */
open class InvocationResult : RemotingSerializable {
    var isSuccessful: Boolean = true
    var exceptionMsg: String? = null
    lateinit var invocation: Invocation

    constructor()
    constructor(isSuccessful: Boolean, exceptionMsg: String?, invocation: Invocation) {
        this.isSuccessful = isSuccessful
        this.exceptionMsg = exceptionMsg
        this.invocation = invocation
    }
}

/**
 * T must be serilizable
 */
class DubboInvocationResult<T> : InvocationResult {

    var returnValue: T? = null
    var returnValueType: Class<*> = Any::class.java

    constructor()
    constructor(isSuccessful: Boolean, exceptionMsg: String?,
                invocation: Invocation,
                returnValueType: Class<*>,
                returnValue: T?
    ) : super(isSuccessful, exceptionMsg, invocation) {
        this.returnValue = returnValue
        this.returnValueType = returnValueType

    }

}


/**
 * RestWebInvocationResult
 */
class RestWebInvocationResult : InvocationResult {
    var status: Int = 200
    var responseHeaders: MutableMap<String, MutableList<String>> = mutableMapOf()
    var responseBody: Any? = null

    constructor()
    constructor(isSuccessful: Boolean, exceptionMsg: String?,
                invocation: Invocation,
                status: Int,
                responseHeaders: MutableMap<String, MutableList<String>>,
                responseBody: Any?
    ) : super(isSuccessful, exceptionMsg, invocation) {
        this.status = status
        this.responseHeaders = responseHeaders
        this.responseBody = responseBody
    }
}