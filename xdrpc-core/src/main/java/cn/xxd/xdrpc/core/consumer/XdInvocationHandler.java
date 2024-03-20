package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.util.MethodUtils;
import cn.xxd.xdrpc.core.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class XdInvocationHandler implements InvocationHandler {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    Class<?> service;

    public XdInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //处理内置方法，如 toString hashCode等Object的基本方法
        String methodName = method.getName();
        if(MethodUtils.checkLocalMethod(methodName)) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);
        System.out.println(rpcResponse);
        //成功或者失败
        if(rpcResponse.isStatus()) {
            //兼容非json的基本类型
            if(rpcResponse.getData() instanceof JSONObject jsonObject) {
                return jsonObject.toJavaObject(method.getReturnType());
            } else if(rpcResponse.getData() instanceof JSONArray jsonArray) {
                Class<?> componentType = method.getReturnType().getComponentType();
                Object[] array = jsonArray.toArray();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    Array.set(resultArray, i, array[i]);
                }
                return resultArray;
            } else {
                return TypeUtils.cast(rpcResponse.getData(), method.getReturnType());
            }
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest) {
        String requestJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(requestJson, JSONTYPE))
                .build();
        try {
            String responseJson = client.newCall(request).execute().body().string();
            RpcResponse response = JSON.parseObject(responseJson, RpcResponse.class);
            return response;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
