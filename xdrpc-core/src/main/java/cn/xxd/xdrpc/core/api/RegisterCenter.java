package cn.xxd.xdrpc.core.api;

import cn.xxd.xdrpc.core.meta.InstanceMeta;
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
    void register(String service, InstanceMeta instance);
    void unRegister(String service, InstanceMeta instance);
    String discover(String serviceName);

    //consumer
    List<InstanceMeta> fetchAll(String service);
    void subscribe(String service, ChangedListener listener);

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
        public void register(String service, InstanceMeta instance) {

        }

        @Override
        public void unRegister(String service, InstanceMeta instance) {

        }

        @Override
        public String discover(String serviceName) {
            return null;
        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }
}
