package cn.xxd.xdrpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class ProviderMeta {
    private Method method;
    private String methodSign;
    Object serviceImpl;
}
