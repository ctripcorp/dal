package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.Configuration;
import qunar.tc.qconfig.client.QTable;
import qunar.tc.qconfig.client.TableConfig;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TableConfigProvider implements ConfigProvider<QTable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableConfigProvider.class);

    private final String configFileName;
    private final AtomicReference<TableConfig> configReference = new AtomicReference<>();
    private final AtomicBoolean isListenerAdded = new AtomicBoolean(false);

    public TableConfigProvider(String configFileName) {
        this.configFileName = configFileName;
    }

    public QTable getConfig() {
        QTable config = null;
        TableConfig tableConfig = getTableConfig();
        if (tableConfig != null) {
            try {
                config = tableConfig.asTable();
            } catch (Exception e) {
                LOGGER.error("Failed to get config in '{}'", configFileName, e);
            }
        }
        return config;
    }

    private TableConfig getTableConfig() {
        TableConfig tableConfig = configReference.get();
        if (null == tableConfig) {
            synchronized (this) {
                tableConfig = configReference.get();
                if (null == tableConfig) {
                    try {
                        tableConfig = TableConfig.get(configFileName);
                    } catch (Exception e) {
                        LOGGER.error("Failed to load '{}' from QConfig", configFileName, e);
                    }
                    if (tableConfig != null) {
                        configReference.set(tableConfig);
                    }
                }
            }
        }
        return tableConfig;
    }

    public void addConfigChangedListener(final ConfigChangedListener<QTable> callback) {
        if (null == callback) {
            return;
        }

        TableConfig tableConfig = getTableConfig();
        if (null == tableConfig) {
            return;
        }

        if (isListenerAdded.compareAndSet(false, true)) {
            tableConfig.addListener(new Configuration.ConfigListener<QTable>() {
                @Override
                public void onLoad(QTable updatedConfig) {
                    if (updatedConfig != null) {
                        callback.onConfigChanged(updatedConfig);
                    }
                }
            });
        }
    }

}
