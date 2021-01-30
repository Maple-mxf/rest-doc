package smartdoc.dashboard.base.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.springframework.data.redis.core.RedisTemplate;
import smartdoc.dashboard.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * The class RestDocAuthImpl
 *
 * @author Maple
 * @since 2.0.RELEASE
 */
public class RestDocAuthImpl implements AuthMetadata {

    private RedisTemplate<String, Object> redisTemplate;

    public static final String CONSOLE_GROUP = "console";

    public static final String VIEW_GROUP = "view";

    private ObjectMapper mapper;

    public RestDocAuthImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
    }


    /**
     * @return auth roles {@link AuthRegistration}
     */
    @Override
    public Collection<AuthRegistration> setupAuthRules() {
        // 添加Default认证组(需要携带Token)
        AuthRegistration consolePageAuthRule = AuthRegistration.builder()
                .group("Default")
                .addAuthPathPattern("/**")
                .setupCredentialFunction(new DefaultBaseCookieVerify())
                .build();

 /*       AuthRegistration viewPageAuthRule = AuthRegistration.builder()
                .group(VIEW_GROUP)
                .addAuthPathPattern("/or/**","/**")
                .setupCredentialFunction()
                .build();*/


        return ImmutableSet.of(consolePageAuthRule);
    }

    private class DefaultBaseCookieVerify extends AbstractCookieCredentialFunction {
        DefaultBaseCookieVerify() {
            super(Token.ACCESS_TOKEN);
        }

        @Override
        public Credential mapCookieToCredential(HttpServletRequest request, Cookie cookie) {
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) redisTemplate.opsForValue().get(cookie.getValue());
            User user = mapper.convertValue(map, User.class);
            if (user == null) return null;

            return Credential.builder(true)
                    .identity(user.getId())
                    .roles(user.getRole())
                    .userInfo(user)
                    .build();
        }
    }
}
