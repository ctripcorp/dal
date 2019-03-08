package com.ctrip.platform.dal.dao.log;

public interface ILogger {
    void logEvent(String type, String name, String message);

    void logTransaction(String type, String name, String message, Callback callback) throws Exception;

    void logTransaction(String type, String name, String message, Callback callback, String failMessage)
            throws Exception;

    void logTransaction(String type, String name, String message, long startTime);

    void logTransaction(String type, String name, String message, Throwable exception, long startTime);

    void info(final String msg);

    void warn(final String msg);

    void warn(final Throwable throwable);

    void error(final String msg, final Throwable e);

}
