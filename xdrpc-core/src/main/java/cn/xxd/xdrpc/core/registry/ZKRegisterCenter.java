package cn.xxd.xdrpc.core.registry;

import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.meta.InstanceMeta;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 服务提供者
 */
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
        client.start();
        System.out.println("zk client started success");
    }

    @Override
    public void stop() {
        client.close();
    }

    @Override
    public void register(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            if(client.checkExists().forPath(servicePath) == null) {
                //创建服务的持久化节点
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            //创建实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            System.out.println("Register to ZK: " + service + " - " + instance + " success!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegister(String service, InstanceMeta instance) {
        String servicePath = "/" + service;
        try {
            //判断服务是否存在
            if(client.checkExists().forPath(servicePath) == null) {
                return;
            }
            //删除实例的临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.delete().quietly().forPath(instancePath);
            System.out.println("unRegister from ZK: " + service + " - " + instance + " success!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String discover(String serviceName) {
        return null;
    }

    @Override
    public List<InstanceMeta> fetchAll(String service) {
        String servicePath = "/" + service;
        try {
            return mapInstances(servicePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private List<InstanceMeta> mapInstances(String servicePath) throws Exception {
        List<String> nodes = client.getChildren().forPath(servicePath);
        return nodes.stream().map(q -> {
            String[] strs = q.split("_");
            return InstanceMeta.http(strs[0], Integer.valueOf(strs[1]));
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(String service, ChangedListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service).setCacheData(true).setMaxDepth(2).build();
        cache.getListenable().addListener((curator, event) -> {
            System.out.println("zk subscribe event: " + event);
            switch (event.getType()) {
                case NODE_ADDED:
                case NODE_REMOVED:
                case NODE_UPDATED:
                    List<InstanceMeta> instances = fetchAll(service);
                    listener.fire(new Event(instances));
                    break;
                default:
                    break;
            }
        });
        cache.start();
    }
}
