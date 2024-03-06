package cn.xxd.xdrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest {
    private String service;  //接口：cn.xxd.xdrpc.demo.api.UserService
    private String method;   //方法
    private Object[] args;   //参数
}
