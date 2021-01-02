package restdoc.client.api.exception;


import restdoc.client.api.ClientAgentVersion;

/**
 * The class DiffVersionException
 *
 * @author Maple
 * @see ClientAgentVersion
 * @since 2.0.RELEASE
 */
public class DiffVersionException extends ClientException {

    public DiffVersionException() {
        super("Console and client version not consistent,Acknowledge version matching,client current maven sdk version: "
                + ClientAgentVersion.getCurrentVersion());
    }

    public DiffVersionException(String message) {
        super(message);
    }
}
