package restdoc.web.base.auth;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({
        HolderKit.class, AuthContext.class
})
public @interface EnableOnepushingAuth {
}
