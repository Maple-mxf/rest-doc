package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import restdoc.web.core.Result
import restdoc.web.core.ok
import restdoc.remoting.common.body.SubmitHttpTaskRequestBody
import restdoc.web.core.schedule.ClientManager
import restdoc.web.core.schedule.ScheduleServerController
import restdoc.web.util.IDUtil

@RestController
class TestController {

    @Autowired
    lateinit var scheduleServerController: ScheduleServerController

    @Autowired
    lateinit var clientManager: ClientManager

    @GetMapping("/echoTcpClient")
    fun echoTcpClient(): Result {

        val channelInfo = clientManager.list().first()

        val requestBody = SubmitHttpTaskRequestBody()
        requestBody.url = "http://39.106.104.216:8090/cloudwebsite/api/idempotent/getToken"
        requestBody.header = mutableMapOf()
        requestBody.uriVar = mutableMapOf()

        val syncSubmitRemoteHttpTask = scheduleServerController.syncSubmitRemoteHttpTask(
                channelInfo.clientId,
                IDUtil.credentialId(),
                requestBody)

        return ok(syncSubmitRemoteHttpTask)
    }
}