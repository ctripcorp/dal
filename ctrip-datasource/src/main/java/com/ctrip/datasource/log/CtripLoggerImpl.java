package com.ctrip.datasource.log;

import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.platform.dal.dao.log.AbstractLogger;
import com.ctrip.platform.dal.dao.log.Callback;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class CtripLoggerImpl extends AbstractLogger {
    public static final String TITLE = "Dal Fx";
    private static final ILog logger = LogManager.getLogger(CtripLoggerImpl.class);

    @Override
    public void logEvent(String type, String name, String message) {
        Cat.logEvent(type, name, Message.SUCCESS, message);
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback) {
        Transaction t = Cat.newTransaction(type, name);
        try {
            t.addData(message);

            if (callback != null)
                callback.execute();

            logEvent(type, name, message);
            t.setStatus(Message.SUCCESS);
        } catch (Throwable e) {
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            t.complete();
        }

    }


    @Override
    public void warn(final String msg) {
        try {
            logger.warn(msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(final String msg, final Throwable e) {
            try {
                Cat.logError(e);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }

            try {
                logger.error(TITLE, e);
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
    }

    @Override
    public void info(final String msg) {
        try {
            logger.info(TITLE, msg);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
