package cn.xxd.xdrpc.core.api;

import cn.xxd.xdrpc.core.meta.InstanceMeta;
import cn.xxd.xdrpc.core.meta.ServiceMeta;
import cn.xxd.xdrpc.core.registry.ChangedListener;

import java.util.List;

/**
 * RegisterCenter
 * @author xiaoxd
 * @create 2024-03-30
 */
public interface RegisterCenter {
    void start();
    void stop();

    //provider
    void register(ServiceMeta service, InstanceMeta instance);
    void unRegister(ServiceMeta service, InstanceMeta instance);
    String discover(ServiceMeta service);

    //consumer
    List<InstanceMeta> fetchAll(ServiceMeta service);
    void subscribe(ServiceMeta service, ChangedListener listener);

    class StaticRegisterCenter implements RegisterCenter {

        List<InstanceMeta> providers;
        public StaticRegisterCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unRegister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public String discover(ServiceMeta service) {
            return null;
        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }
}
