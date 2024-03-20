package cn.xxd.xdrpc.demo.api;


public interface UserService {
    User findById(int id);
    User findById(int id, String name);
    int getId(int id);
    String getName();
    String getName(int id);
}