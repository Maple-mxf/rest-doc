package restdoc.remoting.common;

/**
 * The RequestCode is constant
 */
public interface RequestCode {

    /**
     * Client execute http request task
     */
    @Deprecated
    int SUBMIT_HTTP_PROCESS = 0;

    /**
     * Return the http request execute result
     */
    @Deprecated
    int POST_EXECUTE_RESULT = 1;

    /**
     *
     */
    int REPORT_CLIENT_INFO = 2;

    /**
     *
     */
    int GET_EMPTY_API_TEMPLATES = 3;

    /**
     *
     */
    int REPORT_EXPOSED_API = 4;
}
