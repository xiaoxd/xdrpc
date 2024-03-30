package cn.xxd.xdrpc.core.registry;

import cn.xxd.xdrpc.core.api.RegisterCenter;

import java.util.List;

public class ZKRegisterCenter implements RegisterCenter {
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
        return null;
    }

    @Override
    public void subscribe(String service, String instance) {

    }
}
