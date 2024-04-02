package cn.xxd.xdrpc.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MethodUtils {
    public static boolean checkLocalMethod(String methodName) {
        return Arrays.stream(Object.class.getMethods()).anyMatch(q -> q.getName().equals(methodName));
    }

    public static String methodSign(Method method) {
        StringBuilder methodSign = new StringBuilder(method.getName());
        methodSign.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(q -> methodSign.append("_").append(q.getCanonicalName()));
        return methodSign.toString();
    }

    public static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(annotationClass)) {
                result.add(field);
            }
        }
        aClass = aClass.getSuperclass();
        if(aClass != null) {
            result.addAll(findAnnotatedField(aClass, annotationClass));
        }
        return result;
    }
}
