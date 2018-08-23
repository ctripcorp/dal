package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IdFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdFactory.class);
    private volatile static IdFactory factory = null;
    private static final Object object = new Object();

    private final ConcurrentMap<String, IdWorker> workerCache = new ConcurrentHashMap<>();

    public static IdFactory getInstance() {
        if (null == factory) {
            synchronized (object) {
                if (null == factory) {
                    factory = new IdFactory();
                }
            }
        }
        return factory;
    }

    public IdWorker getOrCreateIdWorker(String sequenceName) {
        if (!ConfigManager.getInstance().getWhitelist().validateSequenceName(sequenceName)) {
            LOGGER.warn("sequenceName [" + sequenceName + "] invalid");
            throw new RuntimeException("sequenceName [" + sequenceName + "] invalid");
        }

        IdWorker worker = workerCache.get(sequenceName);
        if (null == worker) {
            synchronized (this) {
                worker = workerCache.get(sequenceName);
                if (null == worker) {
                    worker = new SnowflakeWorker(sequenceName, ConfigManager.getInstance().getServerConfig());
                    workerCache.put(sequenceName, worker);
                }
            }
        }

        return worker;
    }

}
