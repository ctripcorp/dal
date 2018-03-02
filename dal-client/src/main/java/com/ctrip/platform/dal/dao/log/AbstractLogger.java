package com.ctrip.platform.dal.dao.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLogger implements ILogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLogger.class);

    @Override
    public void logEvent(String type, String name, String message) {
        log(String.format("Type:%s,name:%s,message:%s", type, name, message));
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback) {
        if (callback != null) {
            try {
                callback.execute();
            } catch (Throwable e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        log(String.format("Type:%s,name:%s,message:%s", type, name, message));
    }

    private void log(String info) {
        LOGGER.info(info);
    }

}
