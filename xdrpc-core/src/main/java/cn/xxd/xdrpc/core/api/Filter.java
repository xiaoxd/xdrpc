package cn.xxd.xdrpc.core.api;

/**
 * 过滤器
 *
 * @author xiaoxd
 * @create 2024-03-26
 */
public interface Filter {
    Object preFilter(RpcRequest request);
    Object postFilter(RpcRequest request, RpcResponse response, Object result);

//    Filter next();

    Filter Default = new Filter() {
        @Override
        public Object preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };
}
