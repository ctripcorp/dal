package com.ctrip.platform.dal.dao.log;

import com.ctrip.platform.dal.dao.datasource.log.SqlContext;

import java.util.Map;

public interface ILogger {

    void logEvent(String type, String name, String message);

    void logSqlTransaction(SqlContext sqlContext);

    void logTransaction(String type, String name, String message, Callback callback) throws Exception;

    void logTransaction(String type, String name, String message, Callback callback, String failMessage)
            throws Exception;

    void logTransaction(String type, String name, String message, long startTime);

    void logTransaction(String type, String name, String message, Throwable exception, long startTime);

    void logTransaction(String type, String name, String message, Map<String, String> properties, long startTime);

    void logTransaction(String type, String name, String message, Map<String, String> properties, Throwable exception, long startTime);

    void info(final String msg);

    void warn(final String msg);

    void warn(final Throwable throwable);

    void warn(final String msg, final Throwable e);

    void error(final String msg, final Throwable e);

    void reportError(final String keyName);

    void logRequestContext();

    void logMetric(String metricName, long duration, Map<String, String> tags);

}
