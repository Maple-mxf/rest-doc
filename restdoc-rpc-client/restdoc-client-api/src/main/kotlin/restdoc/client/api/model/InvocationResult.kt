package restdoc.client.api.model

import restdoc.remoting.protocol.RemotingSerializable

/**
 * T must be serilizable
 */
data class InvocationResult<T>(val isNull: Boolean,
                               val returnValue: T?,
                               val returnValueType: Class<*>) : RemotingSerializable()