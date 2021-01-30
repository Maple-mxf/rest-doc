package smartdoc.dashboard.base.auth;

import com.google.common.base.Strings;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

/**
 * @author Maple
 */
public abstract class AbstractCookieCredentialFunction extends AbstractCredentialFunction {

    /**
     * {@link Cookie}
     */
    private String cookieName;

    public AbstractCookieCredentialFunction(String cookieName) {
        com.google.common.base.Verify.verify(!Strings.isNullOrEmpty(cookieName), "cookieName must be set up  require non null");
        this.cookieName = cookieName;
    }

    protected Credential verify(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) return Credential.INVALID_CREDENTIAL;
        return Stream.of(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
                .map(cookie -> this.mapCookieToCredential(request, cookie))
                .orElse(Credential.INVALID_CREDENTIAL);
    }

    /**
     * @param cookie  restdoc.web.base on {@link Cookie} map to CREDENTIAL object instance
     * @param request request obj
     */
    public abstract Credential mapCookieToCredential(HttpServletRequest request, Cookie cookie);
}

