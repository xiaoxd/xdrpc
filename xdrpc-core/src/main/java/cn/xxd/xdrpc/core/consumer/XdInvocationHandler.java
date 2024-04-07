package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.api.Filter;
import cn.xxd.xdrpc.core.api.RpcContext;
import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.consumer.http.OkHttpInvoker;
import cn.xxd.xdrpc.core.meta.InstanceMeta;
import cn.xxd.xdrpc.core.util.MethodUtils;
import cn.xxd.xdrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public class XdInvocationHandler implements InvocationHandler {
    Class<?> service;
    private HttpInvoker httpInvoker = new OkHttpInvoker();
    private RpcContext context;
    private List<InstanceMeta> providers;

    public XdInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //处理内置方法，如 toString hashCode等Object的基本方法
        String methodName = method.getName();
        if (MethodUtils.checkLocalMethod(methodName)) {
            return null;
        }
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        for (Filter filter : this.context.getFilters()) {
            Object preResponse = filter.preFilter(rpcRequest);
            if (preResponse != null) {
                log.debug("Filter pre response: " + preResponse);
                return preResponse;
            }
        }

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        log.debug("Load balance url: " + instance);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        log.debug(String.valueOf(rpcResponse));

        Object result = castResultResult(method, rpcResponse);
        for (Filter filter : this.context.getFilters()) {
            Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
            if (filterResult != null) {
                return filterResult;
            }
        }
        return result;
    }

    private Object castResultResult(Method method, RpcResponse<?> rpcResponse) {
        //成功或者失败
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }
}
