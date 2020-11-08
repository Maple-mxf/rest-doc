package restdoc.web.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import restdoc.web.base.auth.AuthContext;
import restdoc.web.base.auth.Credential;
import restdoc.web.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author maxuefeng
 * @since 2020/5/28
 */
@Component
public class HolderKit {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * @see AuthContext
     */
    private final AuthContext authContext;

    private final
    ObjectMapper mapper;

    public HolderKit(RedisTemplate<String, Object> redisTemplate, AuthContext authContext, ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.authContext = authContext;
        this.mapper = mapper;
    }

    public User getUser() {
        Credential credential = authContext.getCredential(request());
        return Optional.ofNullable(credential)
                .map(c -> mapper.convertValue(c.getUserInfo(), User.class))
                .orElse(null);
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
