package com.ctrip.platform.dal.dao.datasource;

import java.util.Date;

public class SingleDataSourceTask {
    private SingleDataSource singleDataSource;
    private Date enqueueTime;
    private int retryTimes;

    public SingleDataSourceTask(SingleDataSource singleDataSource, Date enqueueTime) {
        this.singleDataSource = singleDataSource;
        this.enqueueTime = enqueueTime;
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

}
