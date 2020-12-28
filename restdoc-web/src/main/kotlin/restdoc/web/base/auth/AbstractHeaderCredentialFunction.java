package restdoc.web.base.auth;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Maple
 */
public abstract class AbstractHeaderCredentialFunction extends AbstractCredentialFunction {

    /**
     * headerKey
     */
    private String headerKey;

    public AbstractHeaderCredentialFunction() {
    }

    public AbstractHeaderCredentialFunction(String headerKey) {
        com.google.common.base.Verify.verify(!Strings.isNullOrEmpty(headerKey),
                "headerKey must be set up  require non null");
        this.headerKey = headerKey;
    }

    protected Credential verify(HttpServletRequest request) {
        String headerValue = request.getHeader(this.headerKey);
        if (Strings.isNullOrEmpty(headerValue)) return Credential.INVALID_CREDENTIAL;
        return mapHeaderValueToCredential(request, headerValue);
    }

    /**
     * @param headerValue restdoc.web.base on {@link org.springframework.http.HttpHeaders} map to CREDENTIAL object instance
     * @return auth Credential
     */
    public abstract Credential mapHeaderValueToCredential(HttpServletRequest request, String headerValue);
}

