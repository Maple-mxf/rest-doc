package restdoc.web.controller.console.rest

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import restdoc.rpc.client.common.model.ApplicationType
import restdoc.rpc.client.common.model.http.HttpApiDescriptor
import restdoc.web.base.auth.Verify
import restdoc.web.controller.console.model.DTreeResVO
import restdoc.web.controller.console.model.DTreeVO
import restdoc.web.controller.console.model.NodeType
import restdoc.web.model.SYS_ADMIN
import restdoc.web.schedule.ApiManager
import restdoc.web.util.MD5Util
import java.nio.charset.StandardCharsets


@RestController
@RequestMapping("/remoteApplication")
@Verify(role = [SYS_ADMIN])
class RemoteApplicationApiController(private val apiManager: ApiManager) {

    @RequestMapping(value = ["/{clientId}/api/list"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun list(@PathVariable clientId: String, @RequestParam at: ApplicationType): Any {

        val apiList = apiManager.list(clientId, at)

        val rootNode = DTreeVO(
                id = "root",
                title = "一级目录(虚拟)",
                parentId = "0",
                type = NodeType.RESOURCE,
                spread = true/*,
                iconClass = "dtree-icon-weibiaoti5"*/)

        val tree = when (at) {
            ApplicationType.REST_WEB -> {
                val descriptors = apiList as List<HttpApiDescriptor>

                val methods = descriptors
                        .map { t ->
                            DTreeVO(
                                    id = MD5Util.MD5Encode(t.pattern, StandardCharsets.UTF_8.name()),
                                    title = t.pattern,
                                    iconClass = "dtree-icon-normal-file",
                                    type = NodeType.API,
                                    parentId = MD5Util.MD5Encode(t.controller, StandardCharsets.UTF_8.name()))
                        }

                val pks = descriptors.groupBy { it.packageName }.keys.map {
                    DTreeVO(
                            id = MD5Util.MD5Encode(it, StandardCharsets.UTF_8.name()),
                            title = it,
                            iconClass = "dtree-icon-weibiaoti5",
                            parentId = "root")
                }

                val controllers = descriptors.groupBy { it.controller }.entries.map {
                    val arr = it.key.split(".")
                    DTreeVO(
                            id = MD5Util.MD5Encode(it.key, StandardCharsets.UTF_8.name()),
                            title = arr.last(),
                            iconClass = "dtree-icon-weibiaoti5",
                            parentId = MD5Util.MD5Encode(it.value.get(0).packageName, StandardCharsets.UTF_8.name()))
                }

                val res = mutableListOf<DTreeVO>()
                res.addAll(methods)
                res.addAll(pks)
                res.addAll(controllers)

                res
            }
            else -> throw NotImplementedError()
        }
        rootNode.children.add(
                DTreeVO(
                        id = "ID",
                        title = "it",
                        iconClass = "dtree-icon-weibiaoti5",
                        parentId = "root")
        )
        tree.add(rootNode)
        return DTreeResVO(data = tree)
    }
}