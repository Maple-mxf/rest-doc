package smartdoc.dashboard.controller.console.view

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import smartdoc.dashboard.controller.console.model.AuthDto
import smartdoc.dashboard.model.User
import java.util.concurrent.TimeUnit
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
open class AdminLoginController {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate
    @Autowired
    lateinit var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    lateinit var holderKit: smartdoc.dashboard.core.HolderKit

    @GetMapping("")
    @smartdoc.dashboard.base.auth.Verify(require = false)
    fun loginView() = if (holderKit.user != null) "redirect:project/view" else "auth/admin_login"

    @GetMapping("/auth")
    @smartdoc.dashboard.base.auth.Verify(require = false)
    fun auth() = if (holderKit.user != null) "redirect:project/view" else "auth/admin_login"

    @PostMapping("/auth")
    fun auth(dto: AuthDto, model: Model, response: HttpServletResponse, request: HttpServletRequest): String {
        val query = Query.query(Criteria("account").`is`(dto.account))
        val user: User? = mongoTemplate.findOne(query, User::class.java)

        model.addAttribute("account", dto.account)
        model.addAttribute("password", dto.password)

        if (user == null) {
            model.addAttribute("errorMessage", "用户不存在")
            return "auth/admin_login"
        }
        val encryptPassword = smartdoc.dashboard.util.MD5Util.encryptPassword(dto.password, dto.account, 1024)
        if (user.password != encryptPassword) {
            model.addAttribute("errorMessage", "账户或者密码错误")
            return "auth/admin_login"
        }

        val cookieValue: String = smartdoc.dashboard.util.MD5Util.MD5Encode(request.remoteHost + user.account, "UTF-8")
        val cookie = Cookie("restdoc_console_access_token", cookieValue)
        val expireTime = 1000 * 60 * 60 * 24 * 7L
        cookie.maxAge = expireTime.toInt()
        response.addCookie(cookie)
        user.password = null
        redisTemplate.opsForValue().set(cookieValue, user)
        redisTemplate.expire(cookieValue, expireTime, TimeUnit.MILLISECONDS)

        return "redirect:index"
    }

    @GetMapping("/index")
    fun main() = "main"

}