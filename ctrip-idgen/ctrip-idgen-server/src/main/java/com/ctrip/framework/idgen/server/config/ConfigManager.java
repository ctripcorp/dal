package com.ctrip.framework.idgen.server.config;

public interface ConfigManager {

    void initialize();

    Whitelist getWhitelist();

    SnowflakeConfig getSnowflakeConfig(String sequenceName);

}
