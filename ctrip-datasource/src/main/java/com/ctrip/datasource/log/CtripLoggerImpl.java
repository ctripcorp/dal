package com.ctrip.datasource.log;

import com.ctrip.datasource.util.CatUtil;
import com.ctrip.framework.clogging.agent.log.ILog;
import com.ctrip.framework.clogging.agent.log.LogManager;
import com.ctrip.framework.clogging.agent.metrics.IMetric;
import com.ctrip.framework.clogging.agent.metrics.MetricManager;
import com.ctrip.framework.ucs.client.api.UcsClient;
import com.ctrip.framework.ucs.client.api.UcsClientFactory;
import com.ctrip.platform.dal.dao.Version;
import com.ctrip.platform.dal.dao.log.AbstractLogger;
import com.ctrip.platform.dal.dao.log.Callback;
import com.ctrip.platform.dal.dao.log.SQLErrorInfo;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

import java.util.Map;
import java.util.Properties;

public class CtripLoggerImpl extends AbstractLogger {

    public static final String TITLE = "Dal Fx";
    private static final ILog logger = LogManager.getLogger(CtripLoggerImpl.class);
    private static final IMetric metric = MetricManager.getMetricer();

    private volatile UcsClient ucsClient;

    @Override
    public void logEvent(String type, String name, String message) {
        Cat.logEvent(type, name, Message.SUCCESS, message);
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback) throws Exception {
        Transaction t = Cat.newTransaction(type, name);
        try {
            t.addData(message);

            if (callback != null)
                callback.execute();

            t.setStatus(Message.SUCCESS);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @Override
    public void logTransaction(String type, String name, String message, Callback callback, String failMessage)
            throws Exception {
        Transaction t = Cat.newTransaction(type, name);
        try {
            t.addData(message);

            if (callback != null)
                callback.execute();

            t.setStatus(failMessage);
        } catch (Exception e) {
            t.setStatus(e);
            Cat.logError(e);
            throw e;
        } finally {
            t.complete();
        }
    }

    @Override
    public void logTransaction(String type, String name, String message, long startTime) {
        logTransaction(type, name, message, (Map<String, String>) null, startTime);
    }

    @Override
    public void logTransaction(String type, String name, String message, Throwable exception, long startTime) {
        logTransaction(type, name, message, (Map<String, String>) null, exception, startTime);
    }

    @Override
    public void logTransaction(String type, String name, String message, Map<String, String> properties, long startTime) {
        Transaction t = Cat.newTransaction(type, name);
        try {
            t.addData(message);
            addProperties(t, properties);
            t.setStatus(Message.SUCCESS);
        } catch (Throwable e) {
            t.setStatus(e);
            Cat.logError(e);
        } finally {
            if (startTime > 0) {
                CatUtil.completeTransaction(t, startTime);
            } else {
                t.complete();
            }
        }
    }

    @Override
    public void logTransaction(String type, String name, String message, Map<String, String> properties, Throwable exception, long startTime) {
        Transaction t = Cat.newTransaction(type, name);
        try {
            t.addData(message);
            t.addData(exception.getMessage());
            addProperties(t, properties);
            t.setStatus(exception);
            Cat.logError(exception);
        } catch (Throwable e) {
            logger.error(e);
        } finally {
            if (startTime > 0) {
                CatUtil.completeTransaction(t, startTime);
            } else {
                t.complete();
            }
        }
    }

    private void addProperties(Transaction transaction, Map<String, String> properties) {
        if (properties != null)
            for (Map.Entry<String, String> entry : properties.entrySet())
                transaction.addProperty(entry.getKey(), entry.getValue());
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
    public void warn(final Throwable throwable) {
        try {
            logger.warn(throwable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void warn(final String msg, final Throwable throwable) {
        try {
            logger.warn(msg, throwable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(final String msg, final Throwable e) {
        try {
            Cat.logError(msg, e);
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

    @Override
    public void reportError(String keyName) {
        String version = Version.getVersion();
        SQLErrorInfo errorInfo = new SQLErrorInfo(version, keyName);
        metric.log(SQLErrorInfo.ERROR, 1, errorInfo.toTag());
    }

    @Override
    public void logRequestContext() {
        UcsClient ucsClient = getOrCreateUcsClient();
        if (ucsClient != null)
            ucsClient.logRequestContextKey();
    }

    private UcsClient getOrCreateUcsClient() {
        if (ucsClient == null) {
            synchronized (this) {
                if (ucsClient == null) {
                    ucsClient = UcsClientFactory.getInstance().getUcsClient();
                }
            }
        }
        return ucsClient;
    }

    @Override
    public void logMetric(String metricName, long duration, Map<String, String> tags) {
        metric.log(metricName, duration, tags);
    }

}
