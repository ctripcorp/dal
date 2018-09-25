package com.ctrip.framework.idgen.server.config;

public interface SnowflakeConfigLocator<T> {

    void setup(T config);

    SnowflakeConfig getSnowflakeConfig(String sequenceName);

}
