package cn.xxd.xdrpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;

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
}
