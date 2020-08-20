package restdoc.base.auth;

import com.google.common.base.Strings;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public abstract class AbstractBeBasedOnURLCredentialFunction extends AbstractCredentialFunction {

    private String urlParamName;

    public AbstractBeBasedOnURLCredentialFunction(String urlParamName) {
        com.google.common.base.Verify.verify(!Strings.isNullOrEmpty(urlParamName),
                "headerKey must be set up  require non null");
        this.urlParamName = urlParamName;

    }

    protected Credential verify(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return mapCookieToCredential(request, queryString);
    }

    /**
     * @param urlParamValue base on url param {@link HttpServletRequest#getQueryString()}
     *
     * @return auth Credential
     */
    protected abstract Credential mapCookieToCredential(HttpServletRequest request,String urlParamValue);
}

