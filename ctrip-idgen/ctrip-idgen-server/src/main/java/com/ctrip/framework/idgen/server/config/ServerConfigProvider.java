package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ServerConfigProvider implements ConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfigProvider.class);
    private static final String SERVER_PROPERTIES = "server.properties";
    private AtomicReference<MapConfig> configReference = new AtomicReference<>();

    public void initialize() {
        try {
            MapConfig config = MapConfig.get(SERVER_PROPERTIES, null);
            if (config != null) {
                configReference.set(config);
            }
        } catch (Throwable t) {
            LOGGER.warn("Load " + SERVER_PROPERTIES + " from QConfig exception", t);
        }
    }

    public Map<String, String> getConfig() {
        MapConfig config = configReference.get();
        if (null == config) {
            return null;
        }

        Map<String, String> map = null;
        try {
            map = config.asMap();
        } catch (Throwable t) {
            LOGGER.error(t.getMessage(), t);
        }
        return map;
    }

    public void addConfigChangedListener(final ConfigChanged callback) {
        final MapConfig config = configReference.get();
        if (null == config || null == callback) {
            return;
        }

        config.addListener(new Configuration.ConfigListener<Map<String, String>>() {
            @Override
            public void onLoad(Map<String, String> map) {
                if (map != null && !map.isEmpty()) {
                    callback.onConfigChanged(map);
                }
            }
        });
    }

}
