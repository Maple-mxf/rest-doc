package restdoc.remoting.common;

/**
 * The RequestCode is constant
 *
 * @author Maple
 * @since 1.0.RELEASE
 */
public interface RequestCode {

    /**
     * acknowledge version
     */
    int ACKNOWLEDGE_VERSION = 1;

    /**
     *
     */
    int GET_CLIENT_INFO = 2;

    /**
     *
     */
    int GET_EMPTY_API_TEMPLATES = 3;

    /**
     *
     */
    int GET_EXPOSED_API = 4;

    /**
     *
     */
    int INVOKE_API = 5;
}
