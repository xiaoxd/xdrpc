package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.api.RpcContext;
import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.util.MethodUtils;
import cn.xxd.xdrpc.core.util.TypeUtils;
import cn.xxd.xdrpc.core.consumer.http.OkHttpInvoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class XdInvocationHandler implements InvocationHandler {
    private HttpInvoker httpInvoker = new OkHttpInvoker();
    private RpcContext context;
    private List<String> providers;
    Class<?> service;

    public XdInvocationHandler(Class<?> service, RpcContext context, List<String> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
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

        List<String> urls = context.getRouter().route(providers);
        String url = context.getLoadBalancer().choose(urls);
        System.out.println("Load balance url: " + url);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, url);
        System.out.println(rpcResponse);
        //成功或者失败
        if(rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }
}
