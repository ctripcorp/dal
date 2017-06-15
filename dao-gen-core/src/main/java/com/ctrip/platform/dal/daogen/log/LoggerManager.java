package com.ctrip.platform.dal.daogen.log;

import com.ctrip.platform.dal.daogen.utils.ServiceLoaderHelper;

public class LoggerManager {
    private static ILogger logger = null;

    // thread safe
    static {
        if (logger == null) {
            try {
                logger = ServiceLoaderHelper.getInstance(ILogger.class);
            } catch (Throwable e) {
            }
        }
    }

    public static ILogger getInstance() {
        return logger;
    }

}
