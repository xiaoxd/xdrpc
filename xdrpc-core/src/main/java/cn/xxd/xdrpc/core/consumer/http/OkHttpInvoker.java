package cn.xxd.xdrpc.core.consumer.http;

import cn.xxd.xdrpc.core.api.RpcRequest;
import cn.xxd.xdrpc.core.api.RpcResponse;
import cn.xxd.xdrpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaoxd
 * @create 2024/4/2
 **/

public class OkHttpInvoker implements HttpInvoker {
    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client;

    public OkHttpInvoker() {
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    public RpcResponse<Object> post(RpcRequest rpcRequest, String url) {
        String requestJson = JSON.toJSONString(rpcRequest);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestJson, JSONTYPE))
                .build();
        try {
            String responseJson = client.newCall(request).execute().body().string();
            RpcResponse<Object> response = JSON.parseObject(responseJson, RpcResponse.class);
            return response;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
