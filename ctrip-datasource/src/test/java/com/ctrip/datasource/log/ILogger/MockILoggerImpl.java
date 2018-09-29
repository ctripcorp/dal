package com.ctrip.datasource.log.ILogger;

import com.ctrip.datasource.log.CtripLoggerImpl;
import com.ctrip.platform.dal.dao.log.Callback;
import org.junit.Assert;

public class MockILoggerImpl extends CtripLoggerImpl {
    @Override
    public void logEvent(String type, String name, String message) {
        if (type == null || type.isEmpty())
            Assert.fail();

        if (name == null || name.isEmpty())
            Assert.fail();

        if (message == null)
            Assert.fail();

        super.logEvent(type, name, message);
        System.out.println(String.format("[logEvent] { type : %s, name : %s, message : %s }", type, name, message));
        Assert.assertTrue(true);
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback) {
        if (type == null || type.isEmpty())
            Assert.fail();

        if (name == null || name.isEmpty())
            Assert.fail();

        if (message == null)
            Assert.fail();

        if (callback == null)
            Assert.fail();

        super.logTransaction(type, name, message, callback);
        System.out
                .println(String.format("[logTransaction] { type: %s, name : %s, message : %s }", type, name, message));
        Assert.assertTrue(true);
    }

    @Override
    public void warn(final String msg) {
        if (msg == null || msg.isEmpty())
            Assert.fail();

        super.warn(msg);
        System.out.println(String.format("[warn] { message : %s }", msg));
        Assert.assertTrue(true);
    }

    @Override
    public void error(final String msg, final Throwable e) {
        if (msg == null)
            Assert.fail();

        if (e == null)
            Assert.fail();

        super.error(msg, e);
        System.out.println(String.format("[error] { message : %s }", msg));
        Assert.assertTrue(true);
    }

    @Override
    public void info(final String msg) {
        if (msg == null)
            Assert.fail();

        super.info(msg);
        System.out.println(String.format("[info] { message : %s }", msg));
        Assert.assertTrue(true);
    }

}
