package com.ctrip.platform.dal.daogen.log;

public interface ILogger {
    void logEvent(String type, String name);

    void error(Throwable e);
}
