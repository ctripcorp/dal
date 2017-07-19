package com.ctrip.platform.dal.daogen.log;

public interface ILogger {
    void logEvent(String type, String name, String status, String nameValuePairs);

    void error(Throwable e);
}
