package com.ctrip.framework.idgen.server.service;

import com.ctrip.framework.idgen.server.config.ConfigManager;
import com.ctrip.framework.idgen.server.config.CtripConfigManager;
import com.ctrip.framework.idgen.server.constant.CatConstants;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdFactory.class);
    private volatile static IdFactory factory = null;

    private final Map<String, IdWorker> workerCache = new ConcurrentHashMap<>();
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
            String msg = String.format("sequenceName '%s' invalid", sequenceName);
            LOGGER.warn(msg);
            throw new IllegalArgumentException(msg);
        }

        sequenceName = sequenceName.trim().toLowerCase();
        IdWorker worker = workerCache.get(sequenceName);
        if (null == worker) {
            synchronized (this) {
                worker = workerCache.get(sequenceName);
                if (null == worker) {
                    worker = new CASSnowflakeWorker(sequenceName,
                            configManager.getSnowflakeConfig(sequenceName));
                    workerCache.put(sequenceName, worker);
                    String msg = String.format("Created worker '%s'", sequenceName);
                    LOGGER.info(msg);
                    Cat.logEvent(CatConstants.CAT_TYPE_IDGEN_SERVER, CatConstants.CAT_NAME_WORKER_CREATED,
                            Event.SUCCESS, msg);
                }
            }
        }

        return worker;
    }

}
