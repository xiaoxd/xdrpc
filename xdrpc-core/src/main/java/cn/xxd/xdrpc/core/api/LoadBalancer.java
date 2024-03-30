package cn.xxd.xdrpc.core.api;

import java.util.List;


/**
 * 负载均衡，权重，随机，轮询，一致性哈希，最小连接数，最小响应时间，自定义
 * LoadBalancer
 */
public interface LoadBalancer<T> {
    T choose(List<T> providers);
    LoadBalancer Default = p -> (p == null || p.isEmpty()) ? null : p.get(0);
}
