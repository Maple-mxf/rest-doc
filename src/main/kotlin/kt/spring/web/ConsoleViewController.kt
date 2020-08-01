package kt.spring.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import restdoc.model.Menu

@Controller
class ConsoleViewController {

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
                        Menu(id = 2,
                                title = "门户CI",
                                type = 1,
                                href = "/console/ci",
                                icon = "layui-icon layui-icon-console"),


                        Menu(id = 3,
                                title = "REST文档",
                                type = 1,
                                href = "/restdoc/docs",
                                icon = "layui-icon layui-icon-console")
                )))

        //
        return menus
    }
}