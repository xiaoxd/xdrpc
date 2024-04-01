package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.consumer.ConsumerBootstrap;
import cn.xxd.xdrpc.core.registry.ZKRegisterCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configurable
public class ProviderConfig {
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrapRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> {
            System.out.println("provider start - begin");
            providerBootstrap.start();
            System.out.println("provider start - end");
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter provider_rc() { return new ZKRegisterCenter(); }
}
