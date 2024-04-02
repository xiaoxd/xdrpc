package cn.xxd.xdrpc.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class TypeUtils {
    public static Object cast(Object origin, Class<?> type) {
        if(origin == null) return null;
        Class<?> originClass = origin.getClass();
        if(type.isAssignableFrom(originClass)) {
            return origin;
        }

        if(type.isArray()) {
            if(origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if(componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                    Array.set(resultArray, i, Array.get(origin, i));
                } else {
                    Object castObject = cast(Array.get(origin, i), componentType);
                    Array.set(resultArray, i, castObject);
                }
            }
            return resultArray;
        }

        if(origin instanceof HashMap map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if(origin instanceof JSONObject jsonObject) {
            return jsonObject.toJavaObject(type);
        }

        if(type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(origin.toString());
        }
        else if(type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(origin.toString());
        }
        else if(type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.valueOf(origin.toString());
        }
        else if(type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(origin.toString());
        }
        else if(type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(origin.toString());
        }
        else if(type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(origin.toString());
        }
        else if(type.equals(Character.class) || type.equals(Character.TYPE)) {
            return Character.valueOf(origin.toString().charAt(0));
        }
        return null;
    }

    public static Object castMethodResult(Method method, Object data) {
        //兼容非json的基本类型
        Class<?> type = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        if(data instanceof JSONObject jsonObject) {
            if(Map.class.isAssignableFrom(type)) {
                Map resultMap = new HashMap();
                if(genericReturnType instanceof ParameterizedType parameterizedType) {
                    Class<?> keyType = (Class<?>)parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>)parameterizedType.getActualTypeArguments()[1];
                    jsonObject.entrySet().stream().forEach(q-> {
                        resultMap.put(cast(q.getKey(), keyType), cast(q.getValue(), valueType));
                    });
                }
            }
            return jsonObject.toJavaObject(type);
        } else if(data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if(type.isArray()) {
                Class<?> componentType = type.getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    Array.set(resultArray, i, array[i]);
                }
                return resultArray;
            }
            else if(List.class.isAssignableFrom(type)) {
                List<Object> resultList = new ArrayList<>(array.length);
                if(genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    for (Object o: array) {
                        resultList.add(TypeUtils.cast(o, (Class<?>) actualType));
                    }
                }
                else {
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            }
            else {
                return null;
            }
        } else {
            return cast(data, type);
        }
    }
}
