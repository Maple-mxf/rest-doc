package restdoc.web.base.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import restdoc.core.Status;
import restdoc.web.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 *
 */
@Component
public class HolderKit {

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    /**
     * @see AuthContext
     */
    @Autowired
    private AuthContext authContext;

    public User getUser() {
        Credential credential = authContext.getCredential(request());
        return Optional.ofNullable(credential)
                .map(c -> (User) c.getUserInfo())
                .orElseThrow(() -> Status.UNAUTHORIZED.instanceError("请先登录"));
    }


    /**
     *
     */
    public HttpServletRequest request() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        return requestAttributes.getRequest();
    }
}
