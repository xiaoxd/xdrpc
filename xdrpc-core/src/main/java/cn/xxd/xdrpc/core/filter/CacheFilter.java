package cn.xxd.xdrpc.core.filter;

import cn.xxd.xdrpc.core.api.Filter;
import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cached filter implementation
 *
 * @author xiaoxd
 * @create 2024/4/7 下午11:18
 **/
public class CacheFilter implements Filter {
    //TODO 替换成guava cache，增加容量和过期时间
    static Map<String, Object> cache = new ConcurrentHashMap<>();
    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
