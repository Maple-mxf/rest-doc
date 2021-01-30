package smartdoc.dashboard.controller.console.rest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import smartdoc.dashboard.controller.console.model.UpdateDubboDocumentDto
import smartdoc.dashboard.core.Result
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.repository.DubboDocumentRepository


/**
 */
@RestController
@smartdoc.dashboard.base.auth.Verify
class DubboDocumentController {

    @Autowired
    lateinit var dubboDocumentRepository: DubboDocumentRepository

    @PatchMapping("/dubboDocument/{id}")
    fun patch(@PathVariable id: String,
              @RequestBody dto: UpdateDubboDocumentDto): Result {

        val oldDocument = dubboDocumentRepository.findById(id).orElseThrow { Status.BAD_REQUEST.instanceError("id参数错误") }

        dto.description?.let {
            oldDocument.desc = it
        }

        dto.paramDescriptor
                .let { descriptor ->
                    val pd = oldDocument.paramDescriptors.first { it.name == descriptor?.name }
                    pd.description = descriptor?.description
                    pd.sampleValue = descriptor?.sampleValue
                    pd.defaultValue = descriptor?.defaultValue
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
    /*@PostMapping("/dubboDocument/{id}/test")
    fun testMicroservice(@PathVariable id: String,
                         @RequestBody params: Map<String, Any?>): Result {

        val document = dubboDocumentRepository.findById(id)
                .orElseThrow { Status.BAD_REQUEST.instanceError("id参数错误") }

        if (params["clientId"] == null) Status.BAD_REQUEST.error()
        val clientId = params["clientId"].toString().replace("tcp://", "")
        val applicationClientInfo = clientRegistryCenter.get(clientId)

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
        val response = scheduleServiceImpl.executeRemotingTask(applicationClientInfo!!.clientId, remotingTask)
        val end = System.currentTimeMillis()

        return if (response.success && response.response != null) {
            val invocationResult = RemotingSerializable.decode(response.response.body, DubboInvocationResult::class.java)
            ok(TestDubboMicroserviceResult(
                    method = document.methodName,
                    paramTypes = document.paramDescriptors.joinToString(separator = ",") { it.type },
                    success = invocationResult.successful,
                    errorMessage = if (invocationResult.exceptionMsg == null || invocationResult.exceptionMsg!!.isBlank()) "无异常信息" else invocationResult.exceptionMsg,
                    returnType = invocationResult.returnValueType,
                    returnValue = if (invocationResult.returnValue == null || invocationResult.returnValue!!.isBlank()) "无返回值" else invocationResult.returnValue,
                    time = end - start
            ))
        } else {
            error("远程服务无响应")
        }
    }*/
}