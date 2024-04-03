package cn.xxd.xdrpc.demo.provider;

import cn.xxd.xdrpc.core.annotation.XdProvider;
import cn.xxd.xdrpc.demo.api.User;
import cn.xxd.xdrpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@XdProvider
public class UserServiceImpl implements UserService {
    @Autowired
    Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "xxd-" + environment.getProperty("server.port") +
                "-" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "xxd-" + environment.getProperty("server.port")
                + "-" + name + System.currentTimeMillis());
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(float id) {
        return 1L;
    }

    @Override
    public long getId(User user) {
        return user.getId();
    }

    @Override
    public String getName() {
        return "xxd-name-" + System.currentTimeMillis();
    }

    @Override
    public String getName(int id) {
        return "dddd" + id;
    }

    @Override
    public int[] getIDs() {
        return new int[]{1, 2, 3};
    }

    @Override
    public int[] getIDs(int[] ids) {
        return ids;
    }

    @Override
    public long[] getLongIDs() {
        return new long[]{11, 22, 33};
    }

    @Override
    public long[] getLongIDs(long[] ids) {
        return ids;
    }

    @Override
    public List<User> getUsers(List<User> users) {
        return users;
    }
}
