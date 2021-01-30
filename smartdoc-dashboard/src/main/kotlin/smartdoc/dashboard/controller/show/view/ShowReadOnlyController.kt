package smartdoc.dashboard.controller.show.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import smartdoc.dashboard.base.auth.Token.ACCESS_TOKEN
import smartdoc.dashboard.controller.show.model.AuthDTO
import smartdoc.dashboard.core.Status
import smartdoc.dashboard.core.failure
import smartdoc.dashboard.core.ok
import smartdoc.dashboard.model.ProjectType
import smartdoc.dashboard.model.User
import smartdoc.dashboard.model.VIEWER
import smartdoc.dashboard.repository.ProjectRepository
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import smartdoc.dashboard.core.Result

@Controller(value = "showReadOnlyController")
@RequestMapping("/or")
class ShowReadOnlyController {

    @Autowired
    lateinit var projectRepository: ProjectRepository

    @Autowired
    lateinit var holderKit: smartdoc.dashboard.core.HolderKit

    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    /**
     * @see Role.VIEWER
     */
    @GetMapping("/{projectId}")
    @smartdoc.dashboard.base.auth.Verify(require = false, role = [VIEWER])
    fun routeEntrance(@PathVariable projectId: String, model: Model): String {
        val project = projectRepository.findById(projectId)
                .orElseThrow {  Status.INVALID_REQUEST.instanceError("id不存在") }

        model.addAttribute("projectId", project.id)
        model.addAttribute("readOnly", true)
        if (!project.allowAccess) return "view/error/403"
        return if (holderKit.user == null) {
            "show/auth"
        } else {
            if (project.type == ProjectType.REST_WEB) "docs/web/api_navigation" else ""
        }
    }

    @PostMapping("/auth")
    @ResponseBody
    fun auth(@RequestBody dto: AuthDTO, request: HttpServletRequest, response: HttpServletResponse, model: Model): Result {
        val project = projectRepository.findById(dto.projectId)
                .orElseThrow {  Status.INVALID_REQUEST.instanceError("id不存在") }
        val encryptPassword = smartdoc.dashboard.util.MD5Util.encryptPassword(dto.password, project.id, 1024)

        return if (encryptPassword == project.accessPassword) {
            val user = User(id = "default", account = "or", password = null, createTime = null, role = VIEWER, teamId = "")
            val cookieValue: String = smartdoc.dashboard.util.MD5Util.MD5Encode(request.remoteHost, "UTF-8")
            val cookie = Cookie(ACCESS_TOKEN, cookieValue)
            cookie.path = "/"
            val expireTime = 1000 * 60 * 60 * 24 * 7L
            cookie.maxAge = expireTime.toInt()
            response.addCookie(cookie)
            redisTemplate.opsForValue().set(cookieValue, user)
            redisTemplate.expire(cookieValue, expireTime, TimeUnit.MILLISECONDS)

            ok()
        } else {
            failure(Status.INVALID_REQUEST, "访问密码错误")
        }
    }

}