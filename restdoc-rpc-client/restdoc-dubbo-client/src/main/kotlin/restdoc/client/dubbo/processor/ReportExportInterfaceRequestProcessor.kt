package restdoc.client.dubbo.processor

import io.netty.channel.ChannelHandlerContext
import restdoc.client.dubbo.DubboContextHolder
import restdoc.remoting.common.RequestCode
import restdoc.remoting.common.body.ClientExportInterfacesBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand

class ReportExportInterfaceRequestProcessor(private val dubboContextHolder: DubboContextHolder) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean = false

    override fun processRequest(ctx: ChannelHandlerContext?, request: RemotingCommand?): RemotingCommand {
        val body = ClientExportInterfacesBody()
        body.exportInterfaces = dubboContextHolder.exportInterfaces
                .map {

                    it.value.

                    val exportInterface = ClientExportInterfacesBody.ExportInterface()
                    exportInterface.name = it.value.beanName

                    val exportMethods = it.value.exportedUrls
                            .flatMap { uri ->
                                val methodsName = uri.getParameter("methods", "").split(",")

                                methodsName.map { methodName ->
                                    val exportMethod = ClientExportInterfacesBody.ExportMethod()
                                    exportMethod.name = methodName

                                    exportMethod
                                }
                            }.toMutableList()

                    exportInterface.exportMethods = exportMethods
                    it.key to exportInterface
                }.toMap().toMutableMap()

        return RemotingCommand.createRequestCommand(RequestCode.SUBMIT_HTTP_PROCESS, null)
    }
}