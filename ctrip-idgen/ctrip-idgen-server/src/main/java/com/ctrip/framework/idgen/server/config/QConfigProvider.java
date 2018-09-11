package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class QConfigProvider implements ConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(QConfigProvider.class);
    private final String configFileName;
    private final AtomicReference<MapConfig> configReference = new AtomicReference<>();
    private volatile boolean isListenerAdded = false;

    public QConfigProvider(String configFileName) {
        this.configFileName = configFileName;
    }

    public void initialize() {
        try {
            MapConfig mapConfig = MapConfig.get(configFileName, null);
            if (mapConfig != null) {
                configReference.set(mapConfig);
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to load '{}' from QConfig", configFileName, t);
        }
    }

    public Map<String, String> getConfig() {
        Map<String, String> config = null;
        MapConfig mapConfig = configReference.get();
        if (mapConfig != null) {
            try {
                config = mapConfig.asMap();
            } catch (Throwable t) {
                LOGGER.error("Failed to get config in '{}'", configFileName, t);
            }
        }
        return config;
    }

    public void addConfigChangedListener(final ConfigChanged callback) {
        if (null == callback) {
            return;
        }
        MapConfig mapConfig = configReference.get();
        if (null == mapConfig) {
            return;
        }

        if (!isListenerAdded) {
            synchronized (this) {
                if (!isListenerAdded) {
                    mapConfig.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                        @Override
                        public void onLoad(Map<String, String> updatedConfig) {
                            if (updatedConfig != null) {
                                callback.onConfigChanged(updatedConfig);
                            }
                        }
                    });
                    isListenerAdded = true;
                }
            }
        }
    }

}
