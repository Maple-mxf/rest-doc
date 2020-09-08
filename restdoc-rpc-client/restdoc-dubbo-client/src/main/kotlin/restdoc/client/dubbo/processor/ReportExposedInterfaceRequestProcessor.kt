package restdoc.client.dubbo.processor

import io.netty.channel.ChannelHandlerContext
import restdoc.client.dubbo.DubboContextHolder
import restdoc.client.dubbo.model.ServiceDescriptor
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.ClientExposedInterfacesBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand

class ReportExposedInterfaceRequestProcessor(private val dubboContextHolder: DubboContextHolder) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false

    override fun processRequest(ctx: ChannelHandlerContext?, request: RemotingCommand?): RemotingCommand {
        val body = ClientExposedInterfacesBody()
        body.exposedInterfaces = dubboContextHolder.exportInterfaces
                .map {
                    val exposedInterface = ClientExposedInterfacesBody.ExposedInterface()
                    exposedInterface.name = it.value.beanName

                    val sd = ServiceDescriptor(it.value.interfaceClass)

                    exposedInterface.exposedMethods = sd.allMethods.map { mh ->
                        ClientExposedInterfacesBody.ExposedMethod(
                                mh.method,
                                mh.paramDesc,
                                mh.compatibleParamSignatures,
                                mh.parameterClasses,
                                mh.returnClass,
                                mh.returnTypes,
                                mh.methodName,
                                mh.isGeneric
                        )
                    }
                    it.key to exposedInterface
                }.toMap().toMutableMap()

        val request = RemotingCommand.createRequestCommand(RequestCode.SUBMIT_HTTP_PROCESS, null)
        request.body = body.encode()

        return request
    }
}