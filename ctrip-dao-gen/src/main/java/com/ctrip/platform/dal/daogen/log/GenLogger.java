package com.ctrip.platform.dal.daogen.log;

import com.dianping.cat.Cat;

public class GenLogger implements ILogger {
    public void error(Throwable e) {
        Cat.logError(e);
    }
}
