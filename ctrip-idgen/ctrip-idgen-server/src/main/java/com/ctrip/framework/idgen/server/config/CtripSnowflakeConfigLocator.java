package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.QTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CtripSnowflakeConfigLocator implements SnowflakeConfigLocator<QTable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripSnowflakeConfigLocator.class);
    private static final String GLOBAL_ROW_KEY = "globalConfigRow";

    private AtomicReference<SnowflakeConfig> globalConfigRef = new AtomicReference<>();
    private Map<String, SnowflakeConfig> sequenceConfigMap = new ConcurrentHashMap<>();
    private Server server;

    public CtripSnowflakeConfigLocator(Server server) {
        this.server = server;
    }

    public void setup(QTable config) {
        SnowflakeConfig globalConfig = new CtripSnowflakeConfig(GLOBAL_ROW_KEY, server);
        globalConfig.load(config.row(GLOBAL_ROW_KEY));
        globalConfigRef.set(globalConfig);
        Map<String, Map<String, String>> rowMap = config.rowMap();
        for (Map.Entry<String, Map<String, String>> entry : rowMap.entrySet()) {
            String key = entry.getKey();
            Map<String, String> value = entry.getValue();
            if (key != null && !GLOBAL_ROW_KEY.equals(key.trim())) {
                SnowflakeConfig sequenceConfig = new CtripSnowflakeConfig(key.trim(), server, globalConfig);
                sequenceConfig.load(value);
                sequenceConfigMap.put(key.trim(), sequenceConfig);
            }
        }
    }

    public SnowflakeConfig getSnowflakeConfig(String sequenceName) {
        SnowflakeConfig sequenceConfig = sequenceConfigMap.get(sequenceName);
        if (null == sequenceConfig) {
            sequenceConfig = globalConfigRef.get();
        }
        return sequenceConfig;
    }

}
