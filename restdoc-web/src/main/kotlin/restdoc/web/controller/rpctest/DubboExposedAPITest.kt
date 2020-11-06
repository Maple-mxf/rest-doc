package restdoc.web.controller.rpctest

import org.springframework.web.bind.annotation.RestController

@RestController
class DubboExposedAPITest {

  /*  @Autowired
    lateinit var clientAPIMemoryUnit: ClientAPIMemoryUnit

    @Autowired
    lateinit var scheduleController: ScheduleController
*/

   /* @GetMapping("/invokeSimpleDubboAPI")
    fun invokeSimpleDubboAPI(): Any {
        val apiList = clientExposedAPIManager.listBy(ApplicationType.DUBBO, "echo-service-provider") as List<DubboExposedAPI>

        val pair = clientExposedAPIManager.dubboExposedExposedAPI
                .filter { it.key.service == "echo-service-provider" }
                .map { it.key.address to it.value }
                .first()

        val remoteAddress = pair.first

        for (api in pair.second) {

            for (method in api.exposedMethods) {
                val request = RemotingCommand.createRequestCommand(RequestCode.INVOKE_API, null)
                method.parameterClasses = arrayOf(String::class.java.name)
//                request.body = n
                val dubboInvocation = DubboInvocation(
                        method.methodName,
                        listOf(ObjectHolder<Any>(String::class.java.name, "HelloWorld")),
                        refName = api.refName,
                        returnType = method.returnClass
                )

                request.body = dubboInvocation.encode()

                val remotingTask = RemotingTask(
                        type = RemotingTaskType.SYNC,
                        request = request,
                        timeoutMills = 100000L,
                        invokeCallback = InvokeCallback { }
                )

                val executeResult = scheduleController.executeRemotingTask(remoteAddress, remotingTask)

                if (executeResult.success) {
                    val result = RemotingSerializable.decode(executeResult.response!!.body, ObjectNode::class.java)
                    println(result)
                }
            }
        }
        return ok()
    }*/
}