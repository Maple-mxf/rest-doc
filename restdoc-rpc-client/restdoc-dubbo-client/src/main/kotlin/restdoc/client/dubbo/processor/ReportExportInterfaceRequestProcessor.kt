package restdoc.client.dubbo.processor

import io.netty.channel.ChannelHandlerContext
import restdoc.client.dubbo.DubboContextHolder
import restdoc.remoting.common.body.ClientExportInterfacesBody
import restdoc.remoting.netty.NettyRequestProcessor
import restdoc.remoting.protocol.RemotingCommand
import kotlin.reflect.KClass

class ReportExportInterfaceRequestProcessor(private val dubboContextHolder: DubboContextHolder) : NettyRequestProcessor {

    override fun rejectRequest(): Boolean {
        return false
    }

    override fun processRequest(ctx: ChannelHandlerContext?, request: RemotingCommand?): RemotingCommand {

        //
        dubboContextHolder.exportInterfaces
                .map {
                    val body = ClientExportInterfacesBody()

                    val exportInterface = ClientExportInterfacesBody.ExportInterface()

                    exportInterface.name = it.value.beanName


                    val exportMethods = it.value.exportedUrls
                            .map { uri ->
                                val methodsName = uri.getParameter("methods", "").split(",")

                                methodsName.map { methodName ->
                                    val exportMethod = ClientExportInterfacesBody.ExportMethod()
                                    exportMethod.name = methodName

                                    exportMethod
                                }
                            }.toMutableList()

                    exportInterface.exportMethods = exportMethods
                }
    }
}