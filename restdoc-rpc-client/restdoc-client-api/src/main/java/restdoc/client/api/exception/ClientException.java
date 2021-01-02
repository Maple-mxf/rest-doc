package restdoc.client.api.exception;

/**
 * the class ClientException
 *
 * @author Maple
 * @see Object
 * @since 2.0.RELEASE
 */
public class ClientException extends Exception {

    public ClientException() {
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
