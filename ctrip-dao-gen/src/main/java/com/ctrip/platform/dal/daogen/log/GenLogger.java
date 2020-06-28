package com.ctrip.platform.dal.daogen.log;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.dianping.cat.Cat;

public class GenLogger implements ILogger {
    private static ILog logger;
    private static final String NAME = "CodeGen";

    static {
        logger = LogManager.getLogger(NAME);
    }

    public void logEvent(String type, String name) {
        try {
            Cat.logEvent(type, name);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void info(String message) {
        try {
            logger.info(message);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void error(Throwable e) {
        try {
            Cat.logError(e);
            logger.error(NAME, e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
