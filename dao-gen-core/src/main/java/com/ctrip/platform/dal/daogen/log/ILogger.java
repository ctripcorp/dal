package com.ctrip.platform.dal.daogen.log;

public interface ILogger {
    void logEvent(String type, String name);

    void info(String message);

    void error(Throwable e);
}
