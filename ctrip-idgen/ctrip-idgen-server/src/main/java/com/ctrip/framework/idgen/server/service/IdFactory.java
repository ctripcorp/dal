package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.ConfigManager;
import com.ctrip.framework.idgen.server.config.CtripConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdFactory.class);
    private volatile static IdFactory factory = null;

    private final ConcurrentMap<String, IdWorker> workerCache = new ConcurrentHashMap<>();
    private final ConfigManager configManager = new CtripConfigManager();

    private IdFactory() {}

    public static IdFactory getInstance() {
        if (null == factory) {
            synchronized (IdFactory.class) {
                if (null == factory) {
                    factory = new IdFactory();
                }
            }
        }
        return factory;
    }

    public void initialize() {
        configManager.initialize();
    }

    public IdWorker getOrCreateIdWorker(String sequenceName) {
        if (!configManager.getWhitelist().validate(sequenceName)) {
            String msg = String.format("sequenceName '{}' invalid", sequenceName);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }

        IdWorker worker = workerCache.get(sequenceName);
        if (null == worker) {
            synchronized (this) {
                worker = workerCache.get(sequenceName);
                if (null == worker) {
                    worker = new CASSnowflakeWorker(sequenceName,
                            configManager.getSnowflakeConfig(sequenceName));
                    workerCache.put(sequenceName, worker);
                    LOGGER.info("Created idWorker (sequenceName: {})", sequenceName);
                }
            }
        }

        return worker;
    }

}
