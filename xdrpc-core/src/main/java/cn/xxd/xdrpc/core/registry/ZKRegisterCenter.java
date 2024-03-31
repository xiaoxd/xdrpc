package cn.xxd.xdrpc.core.registry;

import cn.xxd.xdrpc.core.api.RegisterCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkImpl;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;

public class ZKRegisterCenter implements RegisterCenter {
    private CuratorFramework client = null;
    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("xdrpc")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
    }

    @Override
    public void stop() {
        client.close();
    }

    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            if(client.checkExists().forPath(servicePath) == null) {
                //创建服务的持久化节点
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            //创建实例的临时节点
            client.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath + "/" + instance, "provider".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegister(String service, String instance) {
        String servicePath = "/" + service;
        try {
            //判断服务是否存在
            if(client.checkExists().forPath(servicePath) == null) {
                return;
            }
            //删除实例的临时节点
            client.delete().quietly().forPath(servicePath + "/" + instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String discover(String serviceName) {
        return null;
    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }

    @Override
    public void subscribe(String service, String instance) {

    }
}
