package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.annotation.XdConsumer;
import cn.xxd.xdrpc.core.api.LoadBalancer;
import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.api.Router;
import cn.xxd.xdrpc.core.api.RpcContext;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegisterCenter rc = applicationContext.getBean(RegisterCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.stream().forEach(f -> {
                try{
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if(consumer == null) {
                        consumer = createFromRegister(service, context, rc);
                        f.setAccessible(true);
                        f.set(bean, consumer);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private Object createFromRegister(Class<?> service, RpcContext context, RegisterCenter rc) {

        String serviceName = service.getCanonicalName();
        List<String> nodes = rc.fetchAll(serviceName);
        if(nodes == null || nodes.isEmpty()) {
            System.err.println("no provider found for " + serviceName);
            return null;
        }
        List<String> providers = mapUrls(nodes);
        System.out.println("maps to providers: " + serviceName);
        providers.forEach(System.out::println);
        rc.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrls(event.getNodes()));
        });
        return createConsumer(service, context, providers);
    }

    private static List<String> mapUrls(List<String> nodes) {
        return nodes.stream().map(q -> "http://" + q.replace('_', ':')).collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[] {service}, new XdInvocationHandler(service, context, providers));
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if(field.isAnnotationPresent(XdConsumer.class)) {
                result.add(field);
            }
        }
        aClass = aClass.getSuperclass();
        if(aClass != null) {
            result.addAll(findAnnotatedField(aClass));
        }
        return result;
    }
}
