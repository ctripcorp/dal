package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;

import java.util.Map;
import java.util.Set;

public class DefaultDataSourceConfigureProvider implements IntegratedConfigProvider {

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        DataSourceConfigureParser.getInstance();
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        DataSourceConfigure dataSourceConfigure =
                DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(dbName);
        if (dataSourceConfigure == null)
            return new DataSourceConfigure(dbName);

        return new DataSourceConfigure(dbName, dataSourceConfigure.getProperties());
    }

    @Override
    public void setup(Set<String> dbNames) {}

    @Override
    public void register(String dbName, DataSourceConfigureChangeListener listener) {}

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(String dbName) {
        return getDataSourceConfigure(dbName);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        return null;
    }

}
