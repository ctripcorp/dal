package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

import java.util.Map;
import java.util.Set;

public class DefaultDataSourceConfigureProvider implements IntegratedConfigProvider {

    private ClusterConfigProvider clusterConfigProvider = new LocalClusterConfigProvider();
    private ClusterInfoProvider clusterInfoProvider = new LocalClusterInfoProvider();

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
        return clusterConfigProvider.getClusterConfig(clusterName);
    }

    @Override
    public ClusterInfo getClusterInfo(String databaseKey) {
        return clusterInfoProvider.getClusterInfo(databaseKey);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id) {
        DataSourceConfigure dataSourceConfigure =
                DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(id);
        if (dataSourceConfigure == null)
            return new DataSourceConfigure(id.getId());

        return new DataSourceConfigure(id.getId(), dataSourceConfigure.getProperties());
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(DataSourceIdentity id) {
        return getDataSourceConfigure(id);
    }

    @Override
    public void register(DataSourceIdentity id, DataSourceConfigureChangeListener listener) {}

    @Override
    public void unregister(String dbName) {
    }

    @Override
    public void unregister(DataSourceIdentity id) {
    }

}
