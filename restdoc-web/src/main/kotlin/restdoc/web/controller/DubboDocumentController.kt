package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import restdoc.client.api.model.DubboInvocation
import restdoc.client.api.model.DubboInvocationResult
import restdoc.client.api.model.ObjectHolder
import restdoc.remoting.InvokeCallback
import restdoc.remoting.common.RequestCode
import restdoc.remoting.protocol.RemotingCommand
import restdoc.remoting.protocol.RemotingSerializable
import restdoc.web.controller.obj.TestDubboMicroserviceResult
import restdoc.web.controller.obj.UpdateDubboDocumentDto
import restdoc.web.core.Result
import restdoc.web.core.Status
import restdoc.web.core.ok
import restdoc.web.core.schedule.ClientChannelManager
import restdoc.web.core.schedule.RemotingTask
import restdoc.web.core.schedule.RemotingTaskType
import restdoc.web.core.schedule.ScheduleController
import restdoc.web.repository.DubboDocumentRepository

/**
 */
@RestController
class DubboDocumentController {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @Autowired
    lateinit var clientChannelManager: ClientChannelManager

    @Autowired
    lateinit var scheduleController: ScheduleController

    @PatchMapping("/dubboDocument/{id}")
    fun patch(@PathVariable id: String,
              @RequestBody dto: UpdateDubboDocumentDto): Result {

        val oldDocument = dubboDocumentRepository.findById(id).orElseThrow { Status.BAD_REQUEST.instanceError("id参数错误") }

        dto.description?.let {
            oldDocument.desc = it
        }

        dto.paramDescriptor?.let { descriptor ->
            val pd = oldDocument.paramDescriptors.first { it.name == descriptor.name }
            pd.description = descriptor.description
            pd.sampleValue = descriptor.sampleValue
            pd.defaultValue = descriptor.defaultValue
        }

        dto.returnValueDescriptor?.let {
            oldDocument.returnValueDescriptor.description = it.description
            oldDocument.returnValueDescriptor.sampleValue = it.sampleValue
        }

        dubboDocumentRepository.update(oldDocument)

        return ok(oldDocument)
    }


    /**
     * Invoke remote microservice
     */
    @PostMapping("/dubboDocument/{id}/test")
    fun testMicroservice(@PathVariable id: String,
                         @RequestBody params: Map<String, Any?>): Result {

        val document = dubboDocumentRepository.findById(id)
                .orElseThrow { Status.BAD_REQUEST.instanceError("id参数错误") }

        if (params["clientId"] == null) Status.BAD_REQUEST.error()
        val clientId = params["clientId"].toString().replace("tcp://", "")
        val applicationClientInfo = clientChannelManager.findClient(clientId)

        val request = RemotingCommand.createRequestCommand(RequestCode.INVOKE_API, null)

        val invocation = DubboInvocation()

        invocation.apply {
            this.methodName = document.methodName
            this.parameters = document.paramDescriptors
                    .map { ObjectHolder(it.type, params[it.name]) }

            this.refName = document.javaClassName
            this.returnType = document.returnValueDescriptor.type
        }

        request.body = invocation.encode()

        val remotingTask = RemotingTask(
                type = RemotingTaskType.SYNC,
                request = request,
                timeoutMills = 10000L,
                invokeCallback = InvokeCallback {})

        val start = System.currentTimeMillis()
        // DubboInvocationResult
        val response = scheduleController.executeRemotingTask(applicationClientInfo!!.clientId, remotingTask)
        val end = System.currentTimeMillis()

        return if (response.success && response.response != null) {
            val invocationResult = RemotingSerializable.decode(response.response.body, DubboInvocationResult::class.java)
            ok(TestDubboMicroserviceResult(
                    method = document.methodName,
                    paramTypes = document.paramDescriptors.map { it.type }.joinToString(separator = ","),
                    success = invocationResult.isSuccessful,
                    errorMessage = if (invocationResult.exceptionMsg == null || invocationResult.exceptionMsg!!.isBlank()) "无异常信息" else invocationResult.exceptionMsg,
                    returnType = invocationResult.returnValueType,
                    returnValue = if (invocationResult.returnValue == null || invocationResult.returnValue!!.isBlank()) "无返回值" else invocationResult.returnValue,
                    time = end - start
            ))
        } else {
            error("远程服务无响应")
        }
    }
}