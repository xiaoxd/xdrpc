package cn.xxd.xdrpc.core.api;

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
    void register(String service, String instance);
    void unRegister(String service, String instance);
    String discover(String serviceName);

    //consumer
    List<String> fetchAll(String service);
    void subscribe(String service, String instance);

    class StaticRegisterCenter implements RegisterCenter {

        List<String> providers;
        public StaticRegisterCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unRegister(String service, String instance) {

        }

        @Override
        public String discover(String serviceName) {
            return null;
        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, String instance) {

        }
    }
}
