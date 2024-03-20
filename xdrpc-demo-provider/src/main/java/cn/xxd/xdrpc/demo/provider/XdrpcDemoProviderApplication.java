package cn.xxd.xdrpc.demo.provider;

import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.provider.ProviderBootstrap;
import cn.xxd.xdrpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class XdrpcDemoProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(XdrpcDemoProviderApplication.class, args);
	}

	@Autowired
	ProviderBootstrap providerBootstrap;

	// 使用http + json实现序列化和通信
	@RequestMapping("/")
	public RpcResponse invoke(@RequestBody RpcRequest request) {
		return providerBootstrap.invoke(request);
	}

	@Bean
	ApplicationRunner providerRunner()
	{
		return x-> {
			RpcRequest request = new RpcRequest();
			request.setService("cn.xxd.xdrpc.demo.api.UserService");
			request.setMethodSign("findById@1_int");
			request.setArgs(new Integer[] {100});
			RpcResponse response = invoke(request);
			System.out.println(response.getData());


			request.setService("cn.xxd.xdrpc.demo.api.UserService");
			request.setMethodSign("findById@2_int_java.lang.String");
			request.setArgs(new Object[] {100, "xiaoxd"});
			response = invoke(request);
			System.out.println(response.getData());
		};
	}
}
