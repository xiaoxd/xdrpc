package cn.xxd.xdrpc.demo.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.demo.api.User;
import cn.xxd.xdrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

@Component
@XdProvider
public class UserServiceImpl implements UserService {
    @Override
    public User findById(int id) {
        return new User(id, "xxd-" + System.currentTimeMillis());
    }
}
