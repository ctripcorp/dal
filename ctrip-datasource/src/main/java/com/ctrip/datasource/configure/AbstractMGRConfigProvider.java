package com.ctrip.datasource.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

import java.util.Map;
import java.util.Set;

public abstract class AbstractMGRConfigProvider implements IntegratedConfigProvider {

    abstract public boolean getMGRConfig();

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        return null;
    }

    @Override
    public void setup(Set<String> dbNames) {

    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(name);
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(String dbName) {
        return null;
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(id);
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(DataSourceIdentity id) {
        return null;
    }

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {

    }

    @Override
    public void register(DataSourceIdentity id, DataSourceConfigureChangeListener listener) {

    }

    @Override
    public void unregister(String dbName) {

    }

    @Override
    public void unregister(DataSourceIdentity id) {

    }

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        DataSourceConfigureParser.getInstance();
    }
}
