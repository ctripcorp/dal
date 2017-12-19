package com.ctrip.platform.dal.dao.configure;

public class DataSourceConfigureCollection {
    private DataSourceConfigure configure;
    private DataSourceConfigure failoverConfigure;

    public DataSourceConfigureCollection() {}

    public DataSourceConfigureCollection(DataSourceConfigureCollection collection) {
        if (collection != null) {
            this.configure = collection.getConfigure();
            this.failoverConfigure = collection.getFailoverConfigure();
        }
    }

    public DataSourceConfigureCollection(DataSourceConfigure configure, DataSourceConfigure failoverConfigure) {
        this.configure = configure;
        this.failoverConfigure = failoverConfigure;
    }

    public DataSourceConfigure getConfigure() {
        return configure;
    }

    public void setConfigure(DataSourceConfigure configure) {
        this.configure = configure;
    }

    public DataSourceConfigure getFailoverConfigure() {
        return failoverConfigure;
    }

    public void setFailoverConfigure(DataSourceConfigure failoverConfigure) {
        this.failoverConfigure = failoverConfigure;
    }

}
