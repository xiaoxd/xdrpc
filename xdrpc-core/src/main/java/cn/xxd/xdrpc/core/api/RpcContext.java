package cn.xxd.xdrpc.core.api;


import lombok.Data;

import java.util.List;

@Data
public class RpcContext {
    private Router<String> router;
    private LoadBalancer<String> loadBalancer;
    private List<Filter> filters;
}
