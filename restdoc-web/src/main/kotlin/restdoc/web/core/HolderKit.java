package restdoc.web.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import restdoc.web.base.auth.AuthContext;
import restdoc.web.base.auth.Credential;
import restdoc.web.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author Maple
 */
@Component
public class HolderKit {

    /**
     * @see AuthContext
     */
    private final AuthContext authContext;

    private final
    ObjectMapper mapper;

    public HolderKit(AuthContext authContext, ObjectMapper mapper) {
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
