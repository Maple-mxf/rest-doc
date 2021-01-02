package restdoc.client.api;

import java.lang.annotation.*;

/**
 * @author Maple
 * @since 2.0.RELEASE
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SPI {

    String name();
}
