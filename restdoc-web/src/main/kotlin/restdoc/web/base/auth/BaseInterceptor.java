package restdoc.web.base.auth;

import com.google.common.collect.MapMaker;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * {@link HandlerMethod}
 * 通用方法抽象
 * <p>
 * {@link HandlerInterceptor}
 * {@link Method}
 *
 * @author maxuefeng
 * {@link Annotation}
 * 注解实例调用getClass()方法的结果是一个Proxy对象 具体打印结果是com.sun.proxy.$Proxy72
 * 注解实例调用annotationType()方法的结果是一个正确的Class对象  而非一个Proxy对象
 * @see MapMaker#weakValues()
 * @see MapMaker#weakKeys()
 * @see Annotation#annotationType()
 */
public abstract class BaseInterceptor implements HandlerInterceptor {

    @Nullable
    public <TYPE extends Annotation> TYPE getApiServiceAnnotation(@NonNull Class<TYPE> type, @NonNull Object handler) {
        return Optional.of(handler)
                .filter(h -> h instanceof HandlerMethod)
                .map(h -> (HandlerMethod) h)
                .map(h -> {
                    TYPE annotation = h.getMethodAnnotation(type);
                    return Optional.ofNullable(annotation).orElse(h.getBeanType().getDeclaredAnnotation(type));
                })
                .orElse(null);
    }

    public abstract String[] getExcludePathPatterns();

    public abstract String[] getPathPatterns();

    public abstract int getOrder();
}
