package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.meta.InstanceMeta;
import cn.xxd.xdrpc.core.meta.ProviderMeta;
import cn.xxd.xdrpc.core.meta.ServiceMeta;
import cn.xxd.xdrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Map;

@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    RegisterCenter rc;
    private ApplicationContext applicationContext;
    private LinkedMultiValueMap<String, ProviderMeta> skeletons = new LinkedMultiValueMap<>();
    private InstanceMeta instance;
    @Value("${server.port}")
    private Integer port;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("${app.version}")
    private String version;

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
        instance = InstanceMeta.http(ip, port);
        rc.start();
        skeletons.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        log.info("ProviderBootstrap stop, unregister all services");
        skeletons.keySet().forEach(this::unRegisterService);
        rc.stop();
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = toServiceMeta(service);
        rc.register(serviceMeta, instance);
    }

    private void unRegisterService(String service) {
        RegisterCenter rc = applicationContext.getBean(RegisterCenter.class);
        ServiceMeta serviceMeta = toServiceMeta(service);
        rc.unRegister(serviceMeta, instance);
    }

    private ServiceMeta toServiceMeta(String service) {
        return ServiceMeta.builder().app(app).namespace(namespace).env(env).version(version).name(service).build();
    }

    private void getInterface(Object serviceImpl) {
        Class<?> serviceClass = serviceImpl.getClass().getInterfaces()[0];
        Method[] methods = serviceClass.getMethods();
        for (Method method : methods) {
            if (MethodUtils.checkLocalMethod(method.getName())) continue;
            createProvider(serviceClass, serviceImpl, method);
        }
    }

    private void createProvider(Class<?> serviceClass, Object serviceImpl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder().method(method).serviceImpl(serviceImpl).methodSign(MethodUtils.methodSign(method)).build();
        log.info(String.valueOf(providerMeta));
        skeletons.add(serviceClass.getCanonicalName(), providerMeta);
    }
}
