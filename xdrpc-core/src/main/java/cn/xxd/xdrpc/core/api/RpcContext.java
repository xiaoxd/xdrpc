package cn.xxd.xdrpc.core.api;


import cn.xxd.xdrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

@Data
public class RpcContext {
    private Router<InstanceMeta> router;
    private LoadBalancer<InstanceMeta> loadBalancer;
    private List<Filter> filters;
}
