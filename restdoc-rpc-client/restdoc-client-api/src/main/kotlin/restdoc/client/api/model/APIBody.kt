package restdoc.client.api.model

import restdoc.remoting.common.body.BaseExposedAPIBody
import restdoc.remoting.protocol.RemotingSerializable

/**
 * @author Overman
 */
class APIBody : RemotingSerializable() {

    lateinit var apiList: List<BaseExposedAPIBody>
}