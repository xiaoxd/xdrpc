package cn.xxd.xdrpc.demo.consumer;

import cn.xxd.xdrpc.core.annotation.XdConsumer;
import cn.xxd.xdrpc.core.consumer.ConsumerConfig;
import cn.xxd.xdrpc.demo.api.Order;
import cn.xxd.xdrpc.demo.api.OrderService;
import cn.xxd.xdrpc.demo.api.User;
import cn.xxd.xdrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class XdrpcDemoConsumerApplication {

    @XdConsumer
    UserService userService;
    @XdConsumer
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(XdrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            User user = userService.findById(1);
            System.out.println(user);

            Order order = orderService.findById(100);
            System.out.println(order);

            String string = userService.toString();
            System.out.println(string);

            int id = userService.getId(999);
            System.out.println(id);

            String name = userService.getName();
            System.out.println(name);

//            Order order404 = orderService.findById(404);
//            System.out.println(order404);
        };
    }
}
