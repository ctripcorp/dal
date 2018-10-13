package com.ctrip.framework.idgen.server.config;

import com.ctrip.framework.idgen.server.util.TestUtils;

import java.util.HashMap;
import java.util.Map;

public class TestConfigFactory {

    private static final long DEFAULT_WORKER_ID = 0;

    public Server mockServer() {
        return mockServer(DEFAULT_WORKER_ID);
    }

    public Server mockServer(long workerId) {
        String workerIdKey = TestUtils.getLocalWorkerIdKey();
        String workerIdValue = String.valueOf(workerId);
        Map<String, String> properties = new HashMap<>();
        properties.put(workerIdKey, workerIdValue);
        Server server = new CtripServer();
        server.initialize(properties);
        return server;
    }

    public SnowflakeConfig mockSnowflakeConfig() {
        return mockSnowflakeConfig(DEFAULT_WORKER_ID);
    }

    public SnowflakeConfig mockSnowflakeConfig(long workerId) {
        return mockSnowflakeConfig(workerId, null);
    }

    public SnowflakeConfig mockSnowflakeConfig(Map<String, String> properties) {
        return mockSnowflakeConfig(DEFAULT_WORKER_ID, properties);
    }

    public SnowflakeConfig mockSnowflakeConfig(long workerId, Map<String, String> properties) {
        return mockSnowflakeConfig(mockServer(workerId), null, properties);
    }

    public SnowflakeConfig mockSnowflakeConfig(
            Server server, SnowflakeConfig defaultConfig, Map<String, String> properties) {
        SnowflakeConfig config = new CtripSnowflakeConfig(server, defaultConfig);
        config.load(properties);
        return config;
    }

}
