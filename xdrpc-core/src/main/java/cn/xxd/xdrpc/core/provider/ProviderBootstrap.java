package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Object> skeletons = new HashMap<>();

    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return invokeRequest(request);
    }

    private RpcResponse invokeRequest(RpcRequest request) {
        Object bean = skeletons.get(request.getService());
        try {
            Method method = getMethod(request.getMethod(), bean);
            Object result = method.invoke(bean, request.getArgs());
            return new RpcResponse(true, result);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getMethod(String methodName, Object bean) {
        for (Method method : bean.getClass().getMethods()) {
            if(method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    @PostConstruct  //init method，对应的是PreDestroy
    private void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XdProvider.class);
        providers.forEach((x,y)-> System.out.println(x));
//		skeletons.putAll(providers);

        providers.values().forEach(this::getInterface);
    }

    private void getInterface(Object x) {
        Class<?> interface1 = x.getClass().getInterfaces()[0];
        skeletons.put(interface1.getCanonicalName(), x);
    }
}
