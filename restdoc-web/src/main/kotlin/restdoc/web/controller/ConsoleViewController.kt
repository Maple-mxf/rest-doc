package restdoc.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import restdoc.web.model.Menu

@Controller
class ConsoleViewController {

    @GetMapping("/index1")
    fun index1(): String = "index1"

    @GetMapping("")
    fun index(): String = "index"

    @GetMapping("/menu")
    @ResponseBody
    fun getMenu(): Any {

        // Create First Menu
        val menus = listOf(Menu(id = 1,
                title = "工作空间",
                type = 0,
                openType = null,
                href = "",
                icon = "layui-icon layui-icon-console",
                children = mutableListOf(
                        Menu(id = 3,
                                title = "REST文档",
                                type = 1,
                                href = "/restdoc/docs",
                                icon = "layui-icon layui-icon-console"),

                        Menu(id = 4,
                                title = "My Team",
                                type = 1,
                                href = "/restdoc/team/view",
                                icon = "layui-icon layui-icon-console"),

                        Menu(id = 5,
                                title = "Project",
                                type = 1,
                                href = "/restdoc/project/view",
                                icon = "layui-icon layui-icon-console")
                )))

        //
        return menus
    }
}