package restdoc.base.auth;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public abstract class AbstractBeBasedOnHeaderCredentialFunction extends AbstractCredentialFunction {

    /**
     * headerKey
     */
    private String headerKey;

    public AbstractBeBasedOnHeaderCredentialFunction() {
    }

    public AbstractBeBasedOnHeaderCredentialFunction(String headerKey) {
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
     * @param headerValue base on {@link org.springframework.http.HttpHeaders} map to CREDENTIAL object instance
     * @return auth Credential
     */
    public abstract Credential mapHeaderValueToCredential(HttpServletRequest request, String headerValue);
}

