package restdoc.client.api.model

import restdoc.remoting.common.ApplicationType
import restdoc.remoting.protocol.RemotingSerializable

/**
 * @author Overman
 */
data class ClientInfo(
        /**
         * OS name
         */
        val osname: String,

        /**
         * Hostname
         */
        val hostname:String,

        /**
         * Client types
         */
        val type: ApplicationType,

        /**
         * Application provided service name
         */
        val service: String,

        /**
         * Client service serialization protocol
         */
        val serializationProtocol: String
) : RemotingSerializable()
