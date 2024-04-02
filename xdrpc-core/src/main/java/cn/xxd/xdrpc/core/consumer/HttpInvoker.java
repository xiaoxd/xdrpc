package cn.xxd.xdrpc.core.consumer;

import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;

/**
 * @author xiaoxd
 * @create 2024/4/2
 **/

public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
