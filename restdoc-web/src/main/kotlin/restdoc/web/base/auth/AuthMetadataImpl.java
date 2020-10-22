package restdoc.web.base.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import org.springframework.data.redis.core.RedisTemplate;
import restdoc.web.core.Status;
import restdoc.web.model.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * <p>说明</p>
 *
 * <p>作用</p>
 *
 * <ul>
 *   <li>作用1</li>
 * </ul>
 *
 * @since 2.0
 */
public class AuthMetadataImpl implements AuthMetadata {

    private RedisTemplate<String, Object> redisTemplate;

    private ObjectMapper mapper;

    public AuthMetadataImpl(RedisTemplate<String, Object> redisTemplate,ObjectMapper mapper) {
        this.redisTemplate = redisTemplate;
        this.mapper = mapper;
    }


    /**
     * @return auth roles {@link AuthRegistration}
     */
    @Override
    public Collection<AuthRegistration> setupAuthRules() {
        // 添加Default认证组(需要携带Token)
        AuthRegistration defaultAuthRule = AuthRegistration.builder()
                .group("Default")
                .addAuthPathPattern("/**")
                .setupCredentialFunction(new DefaultBaseCookieVerify())
                .build();

        return ImmutableSet.of(defaultAuthRule);
    }

    private class DefaultBaseCookieVerify extends AbstractCookieCredentialFunction {
        DefaultBaseCookieVerify() {
            super(Token.ACCESS_TOKEN);
        }

        @Override
        public Credential mapCookieToCredential(HttpServletRequest request, Cookie cookie) {
            LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) redisTemplate.opsForValue().get(cookie.getValue());
            User user =  mapper.convertValue(map,User.class);
            if (user == null) Status.UNAUTHORIZED.error();

            return Credential.builder(true)
                    .identity(user.getId())
                    .roles("*")
                    .userInfo(user)
                    .build();
        }
    }

    /*private class DefaultTokenVerify extends AbstractHeaderCredentialFunction {

        public DefaultTokenVerify() {
            super(Token.ACCESS_TOKEN);
        }

        @Override
        public @NonNull
        Credential mapHeaderValueToCredential(@NonNull HttpServletRequest request, @NonNull String headerValue) {
            User user = (User) redisTemplate.opsForValue().get(headerValue);
            if (user == null) Status.UNAUTHORIZED.error();

            return Credential.builder(true)
                    .identity(user.getId())
                    .roles("*")
                    .userInfo(user)
                    .build();
        }

        @Override
        public RuntimeException ifErrorThrowing() {
            return Status.UNAUTHORIZED.instanceError();
        }
    }*/
}
