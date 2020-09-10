package restdoc.web.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import restdoc.remoting.common.ApplicationType
import restdoc.remoting.common.DubboExposedAPI
import restdoc.web.controller.obj.NavNode
import restdoc.web.controller.obj.NodeType
import restdoc.web.controller.obj.ROOT_NAV
import restdoc.web.core.ok
import restdoc.web.core.schedule.ExposedAPIManager

@RestController
class MicroserviceController {

    @Autowired
    lateinit var exposedAPIManager: ExposedAPIManager

    @GetMapping("/microservice/{id}/exposedapi")
    fun getExposedAPI(@PathVariable id: String,
                      @RequestParam ap: String): Any {

        val applicationType = ApplicationType.valueOf(ap.toUpperCase())

        val apiList = exposedAPIManager.listBy(applicationType, id)

        return when (applicationType) {
            ApplicationType.DUBBO -> {
                val dubboAPIList: List<DubboExposedAPI> = apiList as List<DubboExposedAPI>
                val navTree = dubboAPIList.map {
                    val children = it.exposedMethods
                            .map { method ->
                                NavNode(
                                        id = method.methodName + "->" + method.parameterClasses.joinToString(separator = "-"),
                                        title = method.methodName,
                                        field = null,
                                        children = mutableListOf(),
                                        pid = it.name,
                                        type = NodeType.API
                                )
                            }.toMutableList()
                    val node = NavNode(
                            id = it.name,
                            title = it.name,
                            field = null,
                            children = children,
                            pid = ROOT_NAV.id)

                    node.children = children
                    node
                }
                ok(navTree)
            }
            else -> {
                throw Throwable("參數錯誤")
            }
        }
    }
}