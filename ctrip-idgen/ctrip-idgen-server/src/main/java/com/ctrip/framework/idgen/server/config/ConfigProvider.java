package com.ctrip.framework.idgen.server.config;

public interface ConfigProvider<T> {

    T getConfig();

    void addConfigChangedListener(final ConfigChangedListener<T> callback);

}
