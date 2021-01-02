package restdoc.client.api;

/**
 * The class ClientAgentVersion
 *
 * @author Maple
 */
public class ClientAgentVersion {

    private ClientAgentVersion(){}

    /**
     * @since 2.0
     */
    private final static String VERSION_2_0_RELEASE = "2.0.RELEASE";

    /**
     * @since 1.0
     */
    private final static String VERSION_1_0_RELEASE = "1.0.RELEASE";

    /**
     * getCurrentVersion
     *
     * @return current sdk version
     */
    public static String getCurrentVersion() {
        return VERSION_2_0_RELEASE;
    }
}
