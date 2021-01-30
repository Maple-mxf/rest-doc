package smartdoc.dashboard.base.auth;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 */
public class AuthException extends RuntimeException {
    public AuthException(@NonNull String errMsg) {
        super(errMsg);
    }
}
