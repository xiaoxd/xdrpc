package cn.xxd.xdrpc.core.meta;

import lombok.Builder;
import lombok.Data;

/**
 * ·şÎñÃèÊöÀà
 *
 * @author xiaoxd
 * @create 2024/4/3 23:29
 **/

@Data
@Builder
public class ServiceMeta {
    private String app;
    private String namespace;
    private String env;
    private String name;
    private String version;

    public String toPath() {
        return String.format("/%s_%s_%s_%s_%s", name, namespace, env, name, version);
    }
}
