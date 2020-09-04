package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.remoting.common.body.SubmitHttpTaskRequestBody
import restdoc.web.core.Result
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientChannelManager
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.util.IDUtil

@RestController
class TestController {

    @Autowired lateinit var scheduleClientController: ScheduleController

    @Autowired lateinit var clientChannelManager: ClientChannelManager

    @GetMapping("/echoTcpClient")
    fun echoTcpClient(): Result {

        val channelInfo = clientChannelManager.list().first()

        val requestBody = SubmitHttpTaskRequestBody()
        requestBody.url = "http://39.106.104.216:8090/cloudwebsite/api/idempotent/getToken"
        requestBody.header = mutableMapOf()
        requestBody.uriVar = mutableMapOf()

        val syncSubmitRemoteHttpTask = scheduleClientController.syncSubmitRemoteHttpTask(
                channelInfo!!.clientId,
                IDUtil.credentialId(),
                requestBody)

        return ok(syncSubmitRemoteHttpTask)
    }
}