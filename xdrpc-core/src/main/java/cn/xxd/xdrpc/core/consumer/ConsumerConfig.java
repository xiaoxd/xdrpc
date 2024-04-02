package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.api.LoadBalancer;
import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.api.Router;
import cn.xxd.xdrpc.core.cluster.RoundRibonLoadBalancer;
import cn.xxd.xdrpc.core.registry.ZKRegisterCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Configurable
public class ConsumerConfig {

    @Value("${xdrpc.providers}")
    String servers;

    @Bean
    ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(@Autowired ConsumerBootstrap consumerBootstrap) {
        return x -> {
            System.out.println("consumer start - begin");
            consumerBootstrap.start();
            System.out.println("consumer start - end");
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter consumer_rc() {
        return new ZKRegisterCenter();
    }
}
