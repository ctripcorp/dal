package com.ctrip.platform.dal.dao.datasource.log;

public class DataSourceLogContext {

    private volatile boolean hasLogged = false;
    private String readStrategy;
    private long sqlTransactionStartTime;
    private long connectionObtained;

    public boolean isHasLogged() {
        return hasLogged;
    }

    public void setHasLogged(boolean hasLogged) {
        this.hasLogged = hasLogged;
    }

    public String getReadStrategy() {
        return readStrategy;
    }

    public void setReadStrategy(String readStrategy) {
        this.readStrategy = readStrategy;
    }

    public long getSqlTransactionStartTime() {
        return sqlTransactionStartTime;
    }

    public void setSqlTransactionStartTime(long sqlTransactionStartTime) {
        this.sqlTransactionStartTime = sqlTransactionStartTime;
    }

    public long getConnectionObtained() {
        return connectionObtained;
    }

    public void setConnectionObtained(long connectionObtained) {
        this.connectionObtained = connectionObtained;
    }

    public void clear() {
        hasLogged = false;
        readStrategy = null;
        sqlTransactionStartTime = 0;
        connectionObtained = 0;
    }
}
