package cn.xxd.xdrpc.demo.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.demo.api.Order;
import cn.xxd.xdrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

@Component
@XdProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(int id) {
        if (id == 404) {
            throw new RuntimeException("404 exception");
        }
        return new Order(id, "order-" + System.currentTimeMillis());
    }
}
