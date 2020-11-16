package com.ctrip.platform.dal.dao.datasource.monitor;

import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

import java.sql.SQLException;

/**
 * @author c7ch23en
 */
public class DefaultDataSourceMonitorDelegate extends DefaultDataSourceMonitor {

    private Boolean checkStatusResult;
    private Boolean checkAlertFrequencyResult;
    private Runnable checkStatusCallback;
    private Runnable checkAlertFrequencyCallback;

    public DefaultDataSourceMonitorDelegate(DataSourceIdentity dataSourceId) {
        super(dataSourceId);
    }

    public DefaultDataSourceMonitorDelegate(DataSourceIdentity dataSourceId,
                                            Runnable checkStatusCallback, Runnable checkAlertFrequencyCallback) {
        super(dataSourceId);
        this.checkStatusCallback = checkStatusCallback;
        this.checkAlertFrequencyCallback = checkAlertFrequencyCallback;
    }

    @Override
    public void report(SQLException e, boolean isUpdateOperation) {
        reset();
        super.report(e, isUpdateOperation);
    }

    @Override
    protected boolean checkStatus() {
        checkStatusResult = super.checkStatus();
        if (!checkStatusResult && checkStatusCallback != null)
            checkStatusCallback.run();
        return checkStatusResult;
    }

    @Override
    protected boolean checkAlertFrequency() {
        checkAlertFrequencyResult = super.checkAlertFrequency();
        if (checkAlertFrequencyResult && checkAlertFrequencyCallback != null)
            checkAlertFrequencyCallback.run();
        return checkAlertFrequencyResult;
    }

    public Boolean getCheckStatusResult() {
        return checkStatusResult;
    }

    public Boolean getCheckAlertFrequencyResult() {
        return checkAlertFrequencyResult;
    }

    public void reset() {
        checkStatusResult = null;
        checkAlertFrequencyResult = null;
    }

}
