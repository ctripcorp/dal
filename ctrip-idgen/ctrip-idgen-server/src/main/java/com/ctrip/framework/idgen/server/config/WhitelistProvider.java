package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class WhitelistProvider implements ConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistProvider.class);
    private static final String WHITELIST_PROPERTIES = "whitelist.properties";
    private AtomicReference<MapConfig> configReference = new AtomicReference<>();

    public void initialize() {
        try {
            MapConfig config = MapConfig.get(WHITELIST_PROPERTIES, null);
            if (config != null) {
                configReference.set(config);
            }
        } catch (Throwable t) {
            LOGGER.warn("Load " + WHITELIST_PROPERTIES + " from QConfig exception", t);
        }
    }

    public Map<String, String> getConfig() {
        MapConfig config = configReference.get();
        if (null == config) {
            return null;
        }
        return config.asMap();
    }

}
