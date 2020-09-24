package restdoc.client.api.model

import restdoc.remoting.common.body.BaseExposedAPIBody
import restdoc.remoting.protocol.RemotingSerializable

/**
 * @author Overman
 * @since 2020/9/24
 */
class APIBody : RemotingSerializable() {

    lateinit var apiList: List<BaseExposedAPIBody>
}