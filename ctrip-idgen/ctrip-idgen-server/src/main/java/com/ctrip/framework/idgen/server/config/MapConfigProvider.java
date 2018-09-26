package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.MapConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MapConfigProvider implements ConfigProvider<Map<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapConfigProvider.class);

    private final String configFileName;
    private final AtomicReference<MapConfig> configReference = new AtomicReference<>();
    private final AtomicBoolean isListenerAdded = new AtomicBoolean(false);

    public MapConfigProvider(String configFileName) {
        this.configFileName = configFileName;
    }

    public Map<String, String> getConfig() {
        Map<String, String> config = null;
        MapConfig mapConfig = getMapConfig();
        if (mapConfig != null) {
            try {
                config = mapConfig.asMap();
            } catch (Exception e) {
                LOGGER.error("Failed to get config in '{}'", configFileName, e);
            }
        }
        return config;
    }

    private MapConfig getMapConfig() {
        MapConfig mapConfig = configReference.get();
        if (null == mapConfig) {
            synchronized (this) {
                mapConfig = configReference.get();
                if (null == mapConfig) {
                    Transaction transaction = Cat.newTransaction(CatConstants.CAT_TYPE_IDGEN_SERVER,
                            CatConstants.CAT_NAME_QCONFIG_LOAD + ":" + configFileName);
                    try {
                        mapConfig = MapConfig.get(configFileName);
                        if (mapConfig != null) {
                            configReference.set(mapConfig);
                            transaction.setStatus(Transaction.SUCCESS);
                        } else {
                            transaction.setStatus("Null config");
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to load '{}' from QConfig", configFileName, e);
                        transaction.setStatus(e);
                    } finally {
                        transaction.complete();
                    }
                }
            }
        }
        return mapConfig;
    }

    public void addConfigChangedListener(final ConfigChangedListener<Map<String, String>> callback) {
        if (null == callback) {
            return;
        }
        MapConfig mapConfig = getMapConfig();
        if (null == mapConfig) {
            return;
        }
        if (isListenerAdded.compareAndSet(false, true)) {
            mapConfig.addListener(new Configuration.ConfigListener<Map<String, String>>() {
                @Override
                public void onLoad(Map<String, String> updatedConfig) {
                    Transaction transaction = Cat.newTransaction(CatConstants.CAT_TYPE_IDGEN_SERVER,
                            CatConstants.CAT_NAME_QCONFIG_RELOAD + ":" + configFileName);
                    try {
                        if (updatedConfig != null) {
                            callback.onConfigChanged(updatedConfig);
                        }
                        transaction.setStatus(Transaction.SUCCESS);
                    } catch (Exception e) {
                        transaction.setStatus(e);
                    } finally {
                        transaction.complete();
                    }
                }
            });
        }
    }

}
