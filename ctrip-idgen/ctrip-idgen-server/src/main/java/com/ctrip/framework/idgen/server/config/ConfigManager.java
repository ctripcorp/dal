package com.ctrip.framework.idgen.server.config;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigManager.class);
    private volatile static ConfigManager configManager = null;
    private ServerConfig serverConfig = new CtripServerConfig();
    private Whitelist whitelist = new CtripWhitelist();
    private ConfigProvider serverConfigProvider = new ServerConfigProvider();
    private ConfigProvider whitelistProvider = new WhitelistProvider();

    public synchronized static ConfigManager getInstance() {
        if (null == configManager) {
            configManager = new ConfigManager();
        }
        return configManager;
    }

    public void initialize() {
        serverConfigProvider.initialize();
        whitelistProvider.initialize();
        serverConfig.importConfig(serverConfigProvider.getConfig());
        whitelist.importConfig(whitelistProvider.getConfig());
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

}
