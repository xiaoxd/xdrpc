package cn.xxd.xdrpc.core.api;

import java.util.List;

public interface Router<T> {
    Router Default = p -> p;

    List<T> route(List<T> providers);
}
