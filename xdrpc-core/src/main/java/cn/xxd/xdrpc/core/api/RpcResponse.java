package cn.xxd.xdrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    private boolean status; //状态：true
    private T data;         //数据：new User
}
