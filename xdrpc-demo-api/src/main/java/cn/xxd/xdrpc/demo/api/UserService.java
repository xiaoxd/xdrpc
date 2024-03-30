package cn.xxd.xdrpc.demo.api;


import java.util.List;

public interface UserService {
    User findById(int id);
    User findById(int id, String name);
    long getId(long id);
    long getId(float id);
    long getId(User user);
    String getName();
    String getName(int id);
    int[] getIDs();
    int[] getIDs(int[] ids);
    long[] getLongIDs();
    long[] getLongIDs(long[] ids);
    List<User> getUsers(List<User> users);
}