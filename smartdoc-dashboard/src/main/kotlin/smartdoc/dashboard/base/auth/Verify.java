package smartdoc.dashboard.base.auth;

import java.lang.annotation.*;

/**
 * 权限认证
 *
 * @author Maple
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Verify {

    /**
     * 默认任何角色都可以访问接口的角色
     *
     * @return  角色数组
     */
    String[] role() default {"*"};

    /**
     * 默认的认证组
     */
    String group() default "Default";

    /**
     * 必须要求token
     * 如果为false  则如果用户携带token 则
     */
    @Deprecated
    boolean require() default true;
}
