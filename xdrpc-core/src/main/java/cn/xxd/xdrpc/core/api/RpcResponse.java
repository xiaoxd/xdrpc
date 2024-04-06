package cn.xxd.xdrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    private boolean status; //״̬��true
    private T data;         //���ݣ�new User
    private Exception ex;
}
