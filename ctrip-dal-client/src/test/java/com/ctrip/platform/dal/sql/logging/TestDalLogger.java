package com.ctrip.platform.dal.sql.logging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author c7ch23en
 */
public class TestDalLogger extends CtripDalLogger {

    private AtomicInteger errorCount = new AtomicInteger(0);

    @Override
    public void error(String msg, Throwable e) {
        super.error(msg, e);
        errorCount.incrementAndGet();
    }

    public int getErrorCount() {
        return errorCount.get();
    }

    public void clearErrorCount() {
        errorCount.set(0);
    }

}
