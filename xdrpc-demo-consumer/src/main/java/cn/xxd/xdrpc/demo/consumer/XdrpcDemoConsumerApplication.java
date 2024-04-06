package cn.xxd.xdrpc.demo.consumer;

import cn.xxd.xdrpc.core.annotation.XdConsumer;
import cn.xxd.xdrpc.core.consumer.ConsumerConfig;
import cn.xxd.xdrpc.demo.api.OrderService;
import cn.xxd.xdrpc.demo.api.User;
import cn.xxd.xdrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
public class XdrpcDemoConsumerApplication {

    @XdConsumer
    UserService userService;
    @XdConsumer
    OrderService orderService;

//    @Autowired
//    ProviderBootstrap providerBootstrap;

    public static void main(String[] args) {
        SpringApplication.run(XdrpcDemoConsumerApplication.class, args);
    }

//    @RequestMapping("/")
//    public RpcResponse invoke(@RequestBody RpcRequest request) {
//        return providerBootstrap.invoke(request);
//    }

    @GetMapping("/")
    public User findById(int id) {
        return userService.findById(id);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
//            System.out.println(userService.getId(10f));
//            System.out.println(userService.getId(new User(88, "dd")));
//            System.out.println(userService.getId(100));
//
//            System.out.println(userService.findById(1));
//            System.out.println(userService.findById(11, "cindy"));
//            System.out.println(userService.getName(123));
//
//            System.out.println(orderService.findById(100));
//
//            System.out.println(userService.toString());
//
//            System.out.println(userService.getId(999f));
//
//            System.out.println(userService.getName());
//
//            System.out.println(Arrays.toString(userService.getIDs()));
//            System.out.println(Arrays.toString(userService.getLongIDs()));
//            System.out.println(Arrays.toString(userService.getIDs(new int[] {55, 66})));
//            System.out.println(Arrays.toString(userService.getLongIDs(new long[] {77, 88})));
            System.out.println(userService.getUsers(Arrays.asList(
                    new User(1, "a"),
                    new User(2, "b"))));

//            Order order404 = orderService.findById(404);
//            System.out.println(order404);
        };
    }
}
