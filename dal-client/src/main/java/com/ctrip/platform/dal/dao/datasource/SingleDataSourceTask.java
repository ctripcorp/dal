package com.ctrip.platform.dal.dao.datasource;

import java.util.Date;

public class SingleDataSourceTask {
    private SingleDataSource singleDataSource;
    private Date enqueueTime;
    private int retryTimes;
    private boolean executeResult;

    public SingleDataSourceTask(SingleDataSource singleDataSource, Date enqueueTime, int retryTimes) {
        this.singleDataSource = singleDataSource;
        this.enqueueTime = enqueueTime;
        this.retryTimes = retryTimes;
    }

    public SingleDataSource getSingleDataSource() {
        return singleDataSource;
    }

    public void setSingleDataSource(SingleDataSource singleDataSource) {
        this.singleDataSource = singleDataSource;
    }

    public Date getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(Date enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public boolean getExecuteResult() {
        return executeResult;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

}
