package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
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
            LOGGER.error(t.getMessage(), t);
        }
    }

    public Map<String, String> getConfig() {
        Map<String, String> map = null;
        MapConfig config = configReference.get();
        if (config != null) {
            try {
                map = config.asMap();
            } catch (Throwable t) {
                LOGGER.error(t.getMessage(), t);
            }
        }
        return map;
    }

    public void addConfigChangedListener(final ConfigChanged callback) {
        if (null == callback) {
            return;
        }
        MapConfig config = configReference.get();
        if (null == config) {
            return;
        }
        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                if (map != null) {
                    callback.onConfigChanged(map);
                }
            }
        });
    }

}
