package cn.xxd.xdrpc.core.registry;

import cn.xxd.xdrpc.core.api.RegisterCenter;
import cn.xxd.xdrpc.core.meta.InstanceMeta;
import cn.xxd.xdrpc.core.meta.ServiceMeta;
import lombok.SneakyThrows;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务提供者
 */
public class ZKRegisterCenter implements RegisterCenter {

    @Value("${xdrpc.zkService}")
    String zkService;

    @Value("${xdrpc.zkRoot}")
    String zkRoot;
    private CuratorFramework client = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkService)
                .namespace(zkRoot)
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        System.out.println("zk client started success, server[" + zkService + "], namespace[" + zkRoot + "]");
    }

    @Override
    public void stop() {
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = service.toPath();
        try {
            if (client.checkExists().forPath(servicePath) == null) {
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
    public void unRegister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = service.toPath();
        try {
            //判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
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
    public String discover(ServiceMeta serviceName) {
        return null;
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = service.toPath();
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
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, service.toPath()).setCacheData(true).setMaxDepth(2).build();
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
