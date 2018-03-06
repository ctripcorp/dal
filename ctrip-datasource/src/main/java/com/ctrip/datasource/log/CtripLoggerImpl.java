package com.ctrip.datasource.log;

import com.ctrip.platform.dal.dao.log.AbstractLogger;
import com.ctrip.platform.dal.dao.log.Callback;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

public class CtripLoggerImpl extends AbstractLogger {
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
}
