package com.ctrip.platform.dal.dao.log;

public interface ILogger {
    void logEvent(String type, String name, String message);

    void logTransaction(String type, String name, String message, Callback callback);

    void info(final String msg);

    void warn(final String msg);

    void error(final String msg, final Throwable e);

}