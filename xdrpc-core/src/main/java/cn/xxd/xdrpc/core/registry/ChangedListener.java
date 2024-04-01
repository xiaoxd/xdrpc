package cn.xxd.xdrpc.core.registry;

/**
 * @author xiaoxd
 * @create 2024/4/1
 **/

public interface ChangedListener {
    void fire(Event event);
}
