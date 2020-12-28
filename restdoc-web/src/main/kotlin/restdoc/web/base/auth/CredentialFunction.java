package restdoc.web.base.auth;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;

/**
 *
 * @author Maple
 */
@FunctionalInterface
public interface CredentialFunction extends Function<HttpServletRequest, Credential> {

    /**
     * @param request {@link HttpServletRequest}
     * @return {@link Credential#getValid()}
     */
    Credential apply(HttpServletRequest request);

    class EmptyCredentialFunction implements CredentialFunction {
        @Override
        public Credential apply(HttpServletRequest request) {
            return Credential.INVALID_CREDENTIAL;
        }
    }

    /**
     * 自定义异常  开发者可以覆盖当前Exception
     * @return define an exception
     */
    default RuntimeException ifErrorThrowing() {
        return new AuthException("access deny! because you has not access this api interface grant!");
    }
}
