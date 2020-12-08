package restdoc.client.api.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.remoting.protocol.RemotingSerializable
import java.beans.ConstructorProperties

/**
 * @author Overman
 */
@JsonPropertyOrder(value = ["osname", "hostname", "type", "service", "serializationProtocol"])
data class ClientInfo @ConstructorProperties(value = ["osname", "hostname", "type", "service", "serializationProtocol"]) constructor(
        /**
         * OS name
         */
        val osname: String,

        /**
         * Hostname
         */
        val hostname: String,

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
