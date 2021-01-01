package restdoc.client.api.exception;


import restdoc.client.api.ClientAgentVersion;

/**
 * @author Maple
 * @see ClientAgentVersion
 * @since 2.0.RELEASE
 */
public class DiffVersionException extends ClientException {

    public DiffVersionException() {
        super("Console and client version not consistent,Please acknowledge version matching,client current maven sdk version: "
                + ClientAgentVersion.getCurrentVersion());
    }

    public DiffVersionException(String message) {
        super(message);
    }
}
