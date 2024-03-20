package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.meta.ProviderMeta;
import cn.xxd.xdrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private LinkedMultiValueMap<String, ProviderMeta> skeletons = new LinkedMultiValueMap<>();

    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return invokeRequest(request);
    }

    private RpcResponse invokeRequest(RpcRequest request) {
        //排除内置方法
        if(MethodUtils.checkLocalMethod(request.getMethodSign())) {
            return null;
        }

        RpcResponse rpcResponse = new RpcResponse();
        try {
            List<ProviderMeta> providerMetas = skeletons.get(request.getService());
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Object result = providerMeta.getMethod().invoke(providerMeta.getServiceImpl(), request.getArgs());
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optionalProviderMeta = providerMetas.stream().filter(q -> q.getMethodSign().equalsIgnoreCase(methodSign)).findFirst();
        return optionalProviderMeta.orElse(null);
    }

    @PostConstruct  //init method，对应的是PreDestroy
    private void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XdProvider.class);

        providers.values().forEach(this::getInterface);
    }

    private void getInterface(Object x) {
        Class<?> inter = x.getClass().getInterfaces()[0];
        Method[] methods = inter.getMethods();
        for (Method method : methods) {
            if(MethodUtils.checkLocalMethod(method.getName())) {
                continue;
            }
            ProviderMeta provider = createProvider(inter, x, method);
            skeletons.add(inter.getCanonicalName(), provider);
        }
    }

    private ProviderMeta createProvider(Class<?> inter, Object x, Method method) {
        ProviderMeta providerMeta = new ProviderMeta();
        providerMeta.setMethod(method);
        providerMeta.setServiceImpl(x);
        providerMeta.setMethodSign(MethodUtils.methodSign(method));
        System.out.println(providerMeta);
        return providerMeta;
    }
}
