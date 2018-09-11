package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private static final Object lock = new Object();
    private static ConfigManager manager = null;
    private static final String SERVER_CONFIG_FILE_NAME = "server.properties";
    private static final String WHITELIST_FILE_NAME = "whitelist.properties";

    private CtripServerConfig serverConfig = new CtripServerConfig();
    private Whitelist whitelist = new CtripWhitelist();
    private ConcurrentMap<String, SnowflakeConfig> snowflakeConfigCache = new ConcurrentHashMap<>();
    private ConfigProvider serverConfigProvider = new QConfigProvider(SERVER_CONFIG_FILE_NAME);
    private ConfigProvider whitelistProvider = new QConfigProvider(WHITELIST_FILE_NAME);

    public static ConfigManager getInstance() {
        if (null == manager) {
            synchronized (lock) {
                if (null == manager) {
                    manager = new ConfigManager();
                }
            }
        }
        return manager;
    }

    public void initialize() {
        serverConfigProvider.initialize();
        whitelistProvider.initialize();
        serverConfig.load(serverConfigProvider.getConfig());
        whitelist.load(whitelistProvider.getConfig());
        addWhitelistChangedListener();
        //...snowflakeConfigCache
    }

    private void addWhitelistChangedListener() {
        whitelistProvider.addConfigChangedListener(new ConfigChanged() {
            @Override
            public void onConfigChanged(Map<String, String> updatedConfig) {
                whitelist.load(updatedConfig);
            }
        });
    }

    public CtripServerConfig getServerConfig() {
        return serverConfig;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

}
