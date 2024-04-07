package cn.xxd.xdrpc.core.api;

import java.util.List;

/**
 * 路由器
 *
 * @author xiaoxd
 * @create 2024-03-26
 */
public interface Router<T> {
    Router Default = p -> p;

    List<T> route(List<T> providers);
}
