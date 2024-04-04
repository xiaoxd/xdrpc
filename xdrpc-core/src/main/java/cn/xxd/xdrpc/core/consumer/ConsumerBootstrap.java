package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.annotation.XdConsumer;
import cn.xxd.xdrpc.core.api.LoadBalancer;
import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.api.Router;
import cn.xxd.xdrpc.core.api.RpcContext;
import cn.xxd.xdrpc.core.meta.InstanceMeta;
import cn.xxd.xdrpc.core.meta.ServiceMeta;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.xxd.xdrpc.core.util.MethodUtils.findAnnotatedField;

@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    Environment environment;
    private ApplicationContext applicationContext;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("${app.version}")
    private String version;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegisterCenter rc = applicationContext.getBean(RegisterCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass(), XdConsumer.class);
            fields.stream().forEach(f -> {
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createFromRegister(service, context, rc);
                        f.setAccessible(true);
                        f.set(bean, consumer);
                        stub.put(serviceName, consumer);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private ServiceMeta toServiceMeta(String serviceName) {
        return ServiceMeta.builder().app(app).namespace(namespace).env(env).version(version).name(serviceName).build();
    }

    private Object createFromRegister(Class<?> service, RpcContext context, RegisterCenter rc) {
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = toServiceMeta(serviceName);
        List<InstanceMeta> nodes = rc.fetchAll(serviceMeta);
        if (nodes == null || nodes.isEmpty()) {
            log.error("no provider found for " + serviceName);
            return null;
        }
        log.info("maps to providers: " + serviceName);
        nodes.forEach(System.out::println);
        rc.subscribe(serviceMeta, event -> {
            nodes.clear();
            nodes.addAll(event.getNodes());
        });
        return createConsumer(service, context, nodes);
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new XdInvocationHandler(service, context, providers));
    }
}
