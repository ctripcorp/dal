package com.ctrip.platform.dal.daogen.log;

import com.ctrip.platform.dal.daogen.utils.ServiceLoaderHelper;

public class LoggerManager {
    private static final Object LOCK = new Object();
    private static ILogger logger = null;

    static {
        synchronized (LOCK) {
            if (logger == null) {
                try {
                    logger = ServiceLoaderHelper.getInstance(ILogger.class);
                } catch (Throwable e) {
                }
            }
            if (logger == null) {
                try {
                    Class clazz = Class.forName("com.ctrip.platform.dal.daogen.log.GenLogger");
                    logger = (ILogger) clazz.newInstance();
                } catch (Throwable e) {
                }
            }
            if (logger == null) {
                logger = new DefaultLogger();
            }
        }
    }

    public static ILogger getInstance() {
        return logger;
    }

}
