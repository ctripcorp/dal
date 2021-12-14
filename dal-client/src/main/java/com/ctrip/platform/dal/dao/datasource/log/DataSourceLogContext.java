package com.ctrip.platform.dal.dao.datasource.log;

public class DataSourceLogContext {

    private volatile boolean hasLogged = false;
    private String readStrategy;
    private long sqlTransactionStartTime;
    private long connectionObtained;
    private String database;
    private boolean paramsEncryptInSqlContext = true;

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

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setParamsEncryptInSqlContext(boolean paramsEncrypt){
        this.paramsEncryptInSqlContext = paramsEncrypt;
    }

    public boolean getParamsEncryptInSqlContext(){
        return this.paramsEncryptInSqlContext;
    }

    public void clear() {
        hasLogged = false;
        readStrategy = null;
        sqlTransactionStartTime = 0;
        connectionObtained = 0;
        paramsEncryptInSqlContext = true;
    }
}
