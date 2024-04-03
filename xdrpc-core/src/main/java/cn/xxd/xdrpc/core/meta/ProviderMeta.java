package cn.xxd.xdrpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@Builder
public class ProviderMeta {
    Object serviceImpl;
    private Method method;
    private String methodSign;
}
