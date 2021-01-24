package restdoc.client.restweb.context;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;
import restdoc.rpc.client.common.model.FieldType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

final class ResolverUtil {

    private static final Set<Class<?>> SIMPLE_TYPES = ofSet(
            Void.class,
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            String.class,
            BigDecimal.class,
            BigInteger.class,
            Date.class,
            Object.class
    );

    private static final Map<Class<?>, FieldType> TYPE_MAP = new HashMap<Class<?>, FieldType>() {
        {
            this.put(Boolean.class, FieldType.BOOLEAN);
            this.put(boolean.class, FieldType.BOOLEAN);
            this.put(Character.class, FieldType.CHAR);
            this.put(char.class, FieldType.BOOLEAN);
            this.put(Byte.class, FieldType.NUMBER);
            this.put(byte.class, FieldType.NUMBER);
            this.put(Short.class, FieldType.NUMBER);
            this.put(short.class, FieldType.NUMBER);
            this.put(Integer.class, FieldType.NUMBER);
            this.put(int.class, FieldType.NUMBER);
            this.put(Long.class, FieldType.NUMBER);
            this.put(long.class, FieldType.NUMBER);
            this.put(Float.class, FieldType.NUMBER);
            this.put(float.class, FieldType.NUMBER);
            this.put(Double.class, FieldType.NUMBER);
            this.put(double.class, FieldType.NUMBER);
            this.put(String.class, FieldType.STRING);
            this.put(BigDecimal.class, FieldType.NUMBER);
            this.put(BigInteger.class, FieldType.NUMBER);
            this.put(Date.class, FieldType.NUMBER);
            this.put(Object.class, FieldType.OBJECT);
            this.put(MultipartFile.class, FieldType.FILE);
        }
    };

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUE = new HashMap<Class<?>, Object>() {
        {
            this.put(Void.class, null);
            this.put(Boolean.class, false);
            this.put(boolean.class, false);
            this.put(Character.class, 'A');
            this.put(char.class, 'A');
            this.put(Byte.class, 0);
            this.put(byte.class, 0);
            this.put(Short.class, 0);
            this.put(short.class, 0);
            this.put(Integer.class, 0);
            this.put(int.class, 0);
            this.put(Long.class, 0L);
            this.put(long.class, 0L);
            this.put(Float.class, 0.0F);
            this.put(float.class, 0.0F);
            this.put(Double.class, 0.0D);
            this.put(double.class, 0.0D);
            this.put(String.class, "");
            this.put(BigDecimal.class, BigDecimal.valueOf(0L));
            this.put(BigInteger.class, BigInteger.valueOf(0L));
            this.put(Date.class, new Date());
            this.put(Object.class, null);
            // TODO  this.put(MultipartFile.class, null);
        }
    };

    @SafeVarargs
    static <T> Set<T> ofSet(T... eles) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, eles);
        return set;
    }

    static FieldType getType(Class<?> type) {
        return TYPE_MAP.getOrDefault(type, FieldType.MISSING);
    }

    static boolean isPrimitive(Class<?> type) {
        if (type.isPrimitive()) return true;
        return SIMPLE_TYPES.contains(type) || type.equals(MultipartFile.class);
    }

    static boolean isFileType(Class<?> type) {
        return type.equals(MultipartFile.class) ||
                Arrays.asList(type.getInterfaces())
                        .contains(MultipartFile.class);
    }

    static Object getPrimitiveTypeDefaultValue(Class<?> type){
        return PRIMITIVE_DEFAULT_VALUE.get(type);
    }


    static Object instantiate(Class<?> beanType) {
        try {
            Constructor<?>[] constructors = beanType.getConstructors();
            Object dtoInstance = Arrays.stream(constructors)
                    .filter(ct -> ct.getParameterCount() == 0)
                    .findAny()
                    .map(ct -> {
                        try {
                            return ct.newInstance();
                        } catch (Exception ignored) {
                            ignored.printStackTrace();
                            return null;
                        }
                    })
                    .orElse(null);

            if (dtoInstance == null) return null;

            Field[] fields = beanType.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if (isPrimitive(fieldType)) {
                    Object defaultSampleValue = PRIMITIVE_DEFAULT_VALUE.get(fieldType);
                    field.set(dtoInstance, defaultSampleValue);
                } else {
                    Object dtoFieldInstance = instantiate(fieldType);
                    field.set(dtoInstance, dtoFieldInstance);
                }
            }
            return dtoInstance;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    static boolean isViewHandler(HandlerMethod handlerMethod) {
        if (handlerMethod.getBeanType().getDeclaredAnnotation(RestController.class) != null)
            return false;
        return handlerMethod.getMethodAnnotation(ResponseBody.class) == null;
    }

}
