package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceName;

import java.util.Map;
import java.util.Set;

public abstract class AbstractVariableDataSourceConfigureProvider implements IntegratedConfigProvider {

    abstract public Map<String, DalConnectionStringConfigure> getConnectionStrings(Set<String> dbNames) throws Exception;

    @Override
    public void setup(Set<String> dbNames) {
        DalPropertiesManager.getInstance().setup();
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String dbName) {
        return getDataSourceConfigure(new DataSourceName(dbName));
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(String dbName) {
        return getDataSourceConfigure(dbName);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id) {
        DalPoolPropertiesConfigure dataSourceConfigure =
                DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(id);
        if (dataSourceConfigure == null)
            return new DataSourceConfigure(id.getId());

        return new DataSourceConfigure(id.getId(), dataSourceConfigure.getProperties());

    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(DataSourceIdentity id)
    {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(id);
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

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        return null;
    }
}
