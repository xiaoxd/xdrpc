package cn.xxd.xdrpc.core.provider;

import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.meta.ProviderMeta;
import cn.xxd.xdrpc.core.util.MethodUtils;
import cn.xxd.xdrpc.core.util.TypeUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

/**
 * @author xiaoxd
 * @create 2024/4/1
 **/

public class ProviderInvoker {
    private final LinkedMultiValueMap<String, ProviderMeta> skeletons;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeletons = providerBootstrap.getSkeletons();
    }

    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return invokeRequest(request);
    }

    private RpcResponse invokeRequest(RpcRequest request) {
        //排除内置方法
        if(MethodUtils.checkLocalMethod(request.getMethodSign())) {
            return null;
        }

        RpcResponse<Object> rpcResponse = new RpcResponse<>();
        try {
            List<ProviderMeta> providerMetas = skeletons.get(request.getService());
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Object[] args = processArgs(request.getArgs(), providerMeta.getMethod().getParameterTypes());
            Object result = providerMeta.getMethod().invoke(providerMeta.getServiceImpl(), args);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if(args == null || args.length == 0) return args;
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actuals;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        Optional<ProviderMeta> optionalProviderMeta = providerMetas.stream().filter(q -> q.getMethodSign().equalsIgnoreCase(methodSign)).findFirst();
        return optionalProviderMeta.orElse(null);
    }
}
