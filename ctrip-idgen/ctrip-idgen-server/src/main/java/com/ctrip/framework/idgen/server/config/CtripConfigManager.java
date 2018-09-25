package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CtripConfigManager implements ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripConfigManager.class);

    private static final String SERVER_QCONFIG_FILE_NAME = "server.properties";
    private static final String WHITELIST_QCONFIG_FILE_NAME = "whitelist.properties";
    private static final String SNOWFLAKE_QCONFIG_FILE_NAME = "snowflake.properties";

    private ConfigProvider serverConfigProvider = new MapConfigProvider(SERVER_QCONFIG_FILE_NAME);
    private ConfigProvider whitelistConfigProvider = new MapConfigProvider(WHITELIST_QCONFIG_FILE_NAME);
    private ConfigProvider snowflakeConfigProvider = new TableConfigProvider(SNOWFLAKE_QCONFIG_FILE_NAME);
    private Server server = new CtripServer();
    private Whitelist whitelist = new CtripWhitelist();
    private SnowflakeConfigLocator snowflakeConfigLocator = new CtripSnowflakeConfigLocator();

    public void initialize() {
        server.initialize(serverConfigProvider.getConfig());
        whitelist.load(whitelistConfigProvider.getConfig());
        snowflakeConfigLocator.setup(snowflakeConfigProvider.getConfig());
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public SnowflakeConfig getSnowflakeConfig(String sequenceName) {
        return snowflakeConfigLocator.getSnowflakeConfig(sequenceName);
    }

}
