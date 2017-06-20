package com.ctrip.platform.dal.daogen.log;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.dianping.cat.Cat;

public class GenLogger implements ILogger {
    private static ILog logger;
    private static final String name = "CodeGen";

    static {
        logger = LogManager.getLogger(name);
    }

    public void error(Throwable e) {
        try {
            Cat.logError(e);
            logger.error(name, e);
        } catch (Throwable e1) {
        }
    }
}
