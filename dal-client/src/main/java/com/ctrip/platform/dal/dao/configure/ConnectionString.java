package com.ctrip.platform.dal.dao.configure;

public class ConnectionString {
    private String normalConnectionString;
    private String failoverConnectionString;

    public ConnectionString(String normalConnectionString, String failoverConnectionString) {
        this.normalConnectionString = normalConnectionString;
        this.failoverConnectionString = failoverConnectionString;
    }

    public String getNormalConnectionString() {
        return normalConnectionString;
    }

    public void setNormalConnectionString(String normalConnectionString) {
        this.normalConnectionString = normalConnectionString;
    }

    public String getFailoverConnectionString() {
        return failoverConnectionString;
    }

    public void setFailoverConnectionString(String failoverConnectionString) {
        this.failoverConnectionString = failoverConnectionString;
    }

}
