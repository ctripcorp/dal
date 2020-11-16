package com.ctrip.platform.dal.dao.datasource.monitor;

import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.sql.SQLException;
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

    public DefaultDataSourceMonitor(DataSourceIdentity dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Override
    public void report(SQLException e, boolean isUpdateOperation) {
        if (e != null) {
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
