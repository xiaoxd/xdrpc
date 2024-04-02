package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.meta.ProviderMeta;
import cn.xxd.xdrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    RegisterCenter rc;

    private LinkedMultiValueMap<String, ProviderMeta> skeletons = new LinkedMultiValueMap<>();
    private String instance;
    @Value("${server.port}")
    private String port;

    @SneakyThrows
    @PostConstruct  //init method，对应的是PreDestroy
    private void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(XdProvider.class);
        rc = applicationContext.getBean(RegisterCenter.class);
        providers.values().forEach(this::getInterface);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        rc.start();
        skeletons.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        System.out.println("ProviderBootstrap stop, unregister all services");
        skeletons.keySet().forEach(this::unRegisterService);
        rc.stop();
    }

    private void registerService(String service) {
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
