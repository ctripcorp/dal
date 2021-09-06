package com.ctrip.platform.dal.dao.datasource.monitor;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesLocator;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author c7ch23en
 */
public class DefaultDataSourceMonitor implements DataSourceMonitor {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final long ALERT_THRESHOLD_MS = 60000;
    private static final long ALERT_THRESHOLD_SAMPLES = 3;
    private static final long ALERT_INTERVAL_MS = 30000;

    private final DataSourceIdentity dataSourceId;
    private final ContinuousFailureStats totalStats = new ContinuousFailureStats();
    private final ContinuousFailureStats writeStats = new ContinuousFailureStats();
    private final AtomicLong lastAlertTime = new AtomicLong(0);
    private long alertThresholdMs = ALERT_THRESHOLD_MS;
    private long alertThresholdSamples = ALERT_THRESHOLD_SAMPLES;
    private long alertIntervalMs = ALERT_INTERVAL_MS;
    private DalPropertiesLocator locator = DalPropertiesManager.getInstance().getDalPropertiesLocator();
    protected List<Class> ignoreExceptions;

    {
        ignoreExceptions = parseIgnoreExceptions(locator.ignoreExceptionsForDataSourceMonitor());
    }

    public DefaultDataSourceMonitor(DataSourceIdentity dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Override
    public void report(SQLException e, boolean isUpdateOperation) {
        if (!isExecuteException(e)) {
            totalStats.record();
            if (isUpdateOperation)
                writeStats.record();
            if (!checkStatus() && checkAlertFrequency())
                doAlert();
        } else {
            totalStats.clear();
            if (isUpdateOperation)
                writeStats.clear();
        }
    }

    protected boolean isExecuteException(SQLException e) {
        if (e == null)
            return true;

        Throwable t1 = e;
        for (Class clazz : ignoreExceptions)
            while (t1 != null && (t1 instanceof SQLException)) {
                if (clazz.isInstance(t1)) {
                    return true;
                }
                t1 = t1.getCause();
            }

        return false;
    }

    protected List<Class> parseIgnoreExceptions(String exception) {
        List<Class> ignoreExceptions = new ArrayList<>();
        if (StringUtils.isEmpty(exception)) {
            return ignoreExceptions;
        }
        String[] exceptions = exception.split(",");
        for (String s : exceptions) {
            if (StringUtils.isEmpty(s))
                continue;
            try {
                ignoreExceptions.add(Class.forName(s.trim()));
            } catch (Exception e) {
                // no need do something
            }
        }
        return ignoreExceptions;
    }

    protected boolean checkStatus() {
        long now = System.currentTimeMillis();
        long continuousFailureStartTime = totalStats.getStartTime();
        long continuousFailureCount = totalStats.getCount();
        long continuousWriteFailureStartTime = writeStats.getStartTime();
        long continuousWriteFailureCount = writeStats.getCount();
        return !((continuousFailureStartTime > 0 && now - continuousFailureStartTime >= alertThresholdMs &&
                continuousFailureCount >= alertThresholdSamples) ||
                (continuousWriteFailureStartTime > 0 && now - continuousWriteFailureStartTime >= alertThresholdMs &&
                        continuousWriteFailureCount >= alertThresholdSamples));
    }

    protected boolean checkAlertFrequency() {
        synchronized (lastAlertTime) {
            long now = System.currentTimeMillis();
            if (now - lastAlertTime.get() >= alertIntervalMs) {
                lastAlertTime.set(now);
                return true;
            }
            return false;
        }
    }

    protected void doAlert() {
        LOGGER.reportError(dataSourceId.getId());
    }

    protected void setAlertThresholdMs(long alertThresholdMs) {
        this.alertThresholdMs = alertThresholdMs;
    }

    protected void setAlertThresholdSamples(long alertThresholdSamples) {
        this.alertThresholdSamples = alertThresholdSamples;
    }

    protected void setAlertIntervalMs(long alertIntervalMs) {
        this.alertIntervalMs = alertIntervalMs;
    }

}
