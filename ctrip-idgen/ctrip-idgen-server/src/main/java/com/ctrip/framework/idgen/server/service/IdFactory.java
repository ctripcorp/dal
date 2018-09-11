package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdFactory.class);
    private static final Object lock = new Object();
    private static IdFactory factory = null;

    private final ConcurrentMap<String, IdWorker> workerCache = new ConcurrentHashMap<>();

    public static IdFactory getInstance() {
        if (null == factory) {
            synchronized (lock) {
                if (null == factory) {
                    factory = new IdFactory();
                }
            }
        }
        return factory;
    }

    public IdWorker getOrCreateIdWorker(String sequenceName) {
        if (!ConfigManager.getInstance().getWhitelist().validate(sequenceName)) {
            String msg = String.format("sequenceName '{}' invalid", sequenceName);
            LOGGER.warn(msg);
            throw new IllegalArgumentException(msg);
        }

        IdWorker worker = workerCache.get(sequenceName);
        if (null == worker) {
            synchronized (this) {
                worker = workerCache.get(sequenceName);
                if (null == worker) {
                    worker = new SnowflakeWorker(sequenceName, ConfigManager.getInstance().getServerConfig());
                    workerCache.put(sequenceName, worker);
                    LOGGER.info("Created idWorker (sequenceName: '{}')", sequenceName);
                }
            }
        }

        return worker;
    }

}
