package restdoc.client.api;

/**
 * The class ServerProperties
 *
 * @author Maple
 * @since 2.0.RELEASE
 */
public interface ServerProperties {

    /**
     * @return Server host
     */
    String host();

    /**
     * @return Server port
     */
    int port();

    /**
     * @return Application service name
     */
    String service();
}
