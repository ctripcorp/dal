package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public interface ConfigProvider {

    void initialize();

    Map<String, String> getConfig();

    void addConfigChangedListener(final ConfigChanged callback);

}
