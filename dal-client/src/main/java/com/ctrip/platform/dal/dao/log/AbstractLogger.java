package com.ctrip.platform.dal.dao.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class AbstractLogger implements ILogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractLogger.class);

    @Override
    public void logEvent(String type, String name, String message) {
        info(String.format("Type:%s, Name:%s, Message:%s", type, name, message));
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback) throws Exception {
        info(String.format("Type:%s, Name:%s, Message:%s", type, name, message));

        if (callback != null) {
            try {
                callback.execute();
            } catch (Exception e) {
                error(e.getMessage(), e);
                throw e;
            }
        }
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback, String failMessage)
            throws Exception {
        logTransaction(type, name, message, callback);
    }

    @Override
    public void logTransaction(String type, String name, String message, long startTime) {
        info(String.format("Type:%s, Name:%s, Message:%s, StartTime:%s", type, name, message, startTime));
    }

    @Override
    public void logTransaction(String type, String name, String message, Throwable exception, long startTime) {
        error(String.format("Type:%s, Name:%s, Message:%s, StartTime:%s", type, name, message, startTime), exception);
    }

    @Override
    public void logTransaction(String type, String name, String message, Map<String, String> properties, long startTime) {
        logTransaction(type, name, message, startTime);
    }

    @Override
    public void logTransaction(String type, String name, String message, Map<String, String> properties, Throwable exception, long startTime) {
        logTransaction(type, name, message, exception, startTime);
    }

    @Override
    public void warn(final String msg) {
        try {
            LOGGER.warn(msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(final Throwable throwable) {
        try {
            LOGGER.warn("", throwable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(final String msg, final Throwable throwable) {
        try {
            LOGGER.warn(msg, throwable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(final String msg, final Throwable e) {
        try {
            LOGGER.error(e.getMessage(), e);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void info(String msg) {
        try {
            LOGGER.info(msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reportError(String keyName) {
        try {
            LOGGER.info(keyName + "switch fail!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logRequestContext() {}

    @Override
    public void logMetric(String metricName, long duration, Map<String, String> tags) {}

}
