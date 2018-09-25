package com.ctrip.framework.idgen.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qconfig.client.QTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CtripSnowflakeConfigLocator implements SnowflakeConfigLocator<QTable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtripSnowflakeConfigLocator.class);

    private AtomicReference<SnowflakeConfig> globalConfigRef = new AtomicReference<>();
    private Map<String, SnowflakeConfig> sequenceConfigMap = new ConcurrentHashMap<>();

    public void setup(QTable config) {
        //...
    }

    public SnowflakeConfig getSnowflakeConfig(String sequenceName) {
        SnowflakeConfig sequenceConfig = sequenceConfigMap.get(sequenceName);
        if (null == sequenceConfig) {
            sequenceConfig = globalConfigRef.get();
        }
        return sequenceConfig;
    }

}
