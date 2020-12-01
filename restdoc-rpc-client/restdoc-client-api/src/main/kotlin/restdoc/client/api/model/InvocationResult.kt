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
class DubboInvocationResult : InvocationResult {

    var returnValue: String? = ""
    var returnValueType: String = Void::class.java.name

    constructor()
    constructor(isSuccessful: Boolean, exceptionMsg: String?,
                invocation: Invocation,
                returnValueType: String,
                returnValue: String?
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
    constructor(isSuccessful: Boolean,
                exceptionMsg: String?,
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