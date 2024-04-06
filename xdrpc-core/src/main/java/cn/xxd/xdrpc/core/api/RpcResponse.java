package cn.xxd.xdrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    private boolean status; //×´Ì¬£ºtrue
    private T data;         //Êý¾Ý£ºnew User
    private Exception ex;
}
