package com.ctrip.framework.idgen.server.config;

import java.util.Map;

public class ConfigManager {

    private volatile static ConfigManager manager = null;
    private ServerConfig serverConfig = new CtripServerConfig();
    private Whitelist whitelist = new CtripWhitelist();
    private ConfigProvider serverConfigProvider = new ServerConfigProvider();
    private ConfigProvider whitelistProvider = new WhitelistProvider();

    public synchronized static ConfigManager getInstance() {
        if (null == manager) {
            manager = new ConfigManager();
        }
        return manager;
    }

    public void initialize() {
        serverConfigProvider.initialize();
        whitelistProvider.initialize();
        serverConfig.importConfig(serverConfigProvider.getConfig());
        whitelist.importConfig(whitelistProvider.getConfig());
        addWhitelistChangedListener();
    }

    private void addWhitelistChangedListener() {
        whitelistProvider.addConfigChangedListener(new ConfigChanged() {
            @Override
            public void onConfigChanged(Map<String, String> properties) {
                whitelist.refreshConfig(properties);
            }
        });
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }

}
