package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.meta.ProviderMeta;
import cn.xxd.xdrpc.core.util.MethodUtils;
import cn.xxd.xdrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private LinkedMultiValueMap<String, ProviderMeta> skeletons = new LinkedMultiValueMap<>();
    private String instance;
    @Value("${server.port}")
    private String port;

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
            Object[] args = processArgs(request.getArgs(), providerMeta.getMethod().getParameterTypes());
            Object result = providerMeta.getMethod().invoke(providerMeta.getServiceImpl(), args);
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

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if(args == null || args.length == 0) return args;
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actuals;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optionalProviderMeta = providerMetas.stream().filter(q -> q.getMethodSign().equalsIgnoreCase(methodSign)).findFirst();
        return optionalProviderMeta.orElse(null);
    }

    @SneakyThrows
    @PostConstruct  //init method，对应的是PreDestroy
    private void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XdProvider.class);

        providers.values().forEach(this::getInterface);
    }

    @PreDestroy
    public void stop() {
        skeletons.keySet().forEach(this::unRegisterService);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        skeletons.keySet().forEach(this::registerService);
    }

    private void registerService(String service) {
        RegisterCenter rc = applicationContext.getBean(RegisterCenter.class);
        rc.register(service, instance);
    }

    private void unRegisterService(String service) {
        RegisterCenter rc = applicationContext.getBean(RegisterCenter.class);
        rc.unRegister(service, instance);
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
