package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.registry.ZKRegisterCenter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;

import java.util.List;

@Configurable
public class ProviderConfig {
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegisterCenter provider_rc() { return new ZKRegisterCenter(); }
}
