package restdoc.web.base.auth;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public abstract class AbstractCredentialFunction implements CredentialFunction {

    @Override
    public Credential apply(HttpServletRequest request) {
        return verify(request);
    }

    protected abstract Credential verify(HttpServletRequest request);
}
