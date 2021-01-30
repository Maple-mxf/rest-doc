package smartdoc.dashboard.util;

import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.annotation.Transient;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Maple
 */
@Deprecated
public class ReflectUtil {

    public static List<Class<?>> getClasses(String packageName) throws IOException {
        List<Class<?>> classes = Lists.newArrayList();
        boolean recursive = true;
        String packageDirName = packageName.replace('.', '/');
        Enumeration dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
        while (true) {
            label58:
            while (dirs.hasMoreElements()) {
                URL url = (URL) dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    Enumeration entries = jar.entries();

                    while (true) {
                        JarEntry entry;
                        String name;
                        int idx;
                        do {
                            do {
                                if (!entries.hasMoreElements()) {
                                    continue label58;
                                }

                                entry = (JarEntry) entries.nextElement();
                                name = entry.getName();
                                if (name.charAt(0) == '/') {
                                    name = name.substring(1);
                                }
                            } while (!name.startsWith(packageDirName));

                            idx = name.lastIndexOf(47);
                            if (idx != -1) {
                                packageName = name.substring(0, idx).replace('/', '.');
                            }
                        } while (idx == -1 && !recursive);

                        if (name.endsWith(".class") && !entry.isDirectory()) {
                            String className = name.substring(packageName.length() + 1, name.length() - 6);

                            try {
                                classes.add(Class.forName(packageName + '.' + className));
                            } catch (ClassNotFoundException var14) {
                                var14.printStackTrace();
                            }
                        }
                    }
                }
            }
            return classes;
        }
    }

    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, boolean recursive, List<Class<?>> classes) {
        File dir = new File(packagePath);
        if (dir.exists() && dir.isDirectory()) {
            File[] dirFiles = dir.listFiles((filex) -> recursive && filex.isDirectory() || filex.getName().endsWith(".class"));
            if (dirFiles != null) {
                int var7 = dirFiles.length;
                for (File file : dirFiles) {
                    if (file.isDirectory()) {
                        findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
                    } else {
                        String className = file.getName().substring(0, file.getName().length() - 6);

                        try {
                            classes.add(Class.forName(packageName + '.' + className));
                        } catch (ClassNotFoundException var12) {
                            var12.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    /**
     * @param type 获取当前class对应的字段和父类对应的字段
     */
    public static List<Field> getFields(@NonNull Class<?> type) {
        List<Field> fieldList = new ArrayList<>();
        for (; type != Object.class; type = type.getSuperclass()) {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod)) continue;
                Transient annotation = field.getDeclaredAnnotation(Transient.class);
                if (annotation != null) continue;
                fieldList.add(field);
            }
        }
        return fieldList;
    }

    /**
     * 将obj2的值赋值给obj1
     */
    public static void swapValue(@NonNull Object obj1, @NonNull Object obj2) throws IllegalAccessException {
        List<Field> fields = getFields(obj2.getClass());

        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(obj2);
            if (value != null) {
                field.set(obj1, value);
            }
        }
    }
}
