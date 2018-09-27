package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public class CtripConfigManager implements ConfigManager {

    private static final String SERVER_CONFIG_FILE_NAME = "server.properties";
    private static final String WHITELIST_CONFIG_FILE_NAME = "whitelist.properties";
    private static final String SNOWFLAKE_CONFIG_FILE_NAME = "snowflake.t";

    private ConfigProvider serverConfigProvider = new MapConfigProvider(SERVER_CONFIG_FILE_NAME);
    private ConfigProvider whitelistConfigProvider = new MapConfigProvider(WHITELIST_CONFIG_FILE_NAME);
    private ConfigProvider snowflakeConfigProvider = new TableConfigProvider(SNOWFLAKE_CONFIG_FILE_NAME);
    private Server server = new CtripServer();
    private Whitelist whitelist = new CtripWhitelist();
    private SnowflakeConfigLocator snowflakeConfigLocator;

    public void initialize() {
        server.initialize(serverConfigProvider.getConfig());
        whitelist.load(whitelistConfigProvider.getConfig());
        snowflakeConfigLocator = new CtripSnowflakeConfigLocator(server);
        snowflakeConfigLocator.setup(snowflakeConfigProvider.getConfig());
        addWhitelistChangedListener();
    }

    private void addWhitelistChangedListener() {
        whitelistConfigProvider.addConfigChangedListener(new ConfigChangedListener<Map<String, String>>() {
            @Override
            public void onConfigChanged(Map<String, String> updatedConfig) {
                whitelist.load(updatedConfig);
            }
        });
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

    public SnowflakeConfig getSnowflakeConfig(String sequenceName) {
        return snowflakeConfigLocator.getSnowflakeConfig(sequenceName);
    }

}
