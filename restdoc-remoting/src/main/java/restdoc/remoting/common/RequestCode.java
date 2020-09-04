package restdoc.remoting.common;

/**
 * The RequestCode is constant
 */
public interface RequestCode {

    /**
     * Client execute http request task
     */
    int SUBMIT_HTTP_PROCESS = 0;

    /**
     * Return the http request execute result
     */
    int POST_EXECUTE_RESULT = 1;

    /**
     *
     */
    int REPORT_CLIENT_INFO = 2;
}
