package com.ctrip.datasource.titan;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.datasource.configure.CtripLocalClusterInfoProvider;
import com.ctrip.datasource.net.HttpExecutor;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.datasource.configure.ClusterInfoProvider;
import com.ctrip.datasource.configure.CtripClusterInfoProvider;
import com.ctrip.datasource.configure.CtripLocalClusterConfigProvider;
import com.ctrip.datasource.configure.qconfig.CtripClusterConfigProvider;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.datasource.DataSourceName;

public class TitanProvider implements IntegratedConfigProvider {

    private static final String USE_LOCAL_CONFIG = "useLocalConfig";
    private DataSourceConfigureManager dataSourceConfigureManager = DataSourceConfigureManager.getInstance();
    private SourceType sourceType = SourceType.Remote;
    private DalPropertiesManager dalPropertiesManager = DalPropertiesManager.getInstance();
    private ClusterConfigProvider clusterConfigProvider = new CtripClusterConfigProvider();
    private ClusterInfoProvider clusterInfoProvider = new CtripClusterInfoProvider(DalPropertiesManager.getInstance().getDalPropertiesLocator(), HttpExecutor.getInstance());

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        setSourceType(settings);
        dataSourceConfigureManager.initialize(settings);
        if (sourceType == SourceType.Local) {
            clusterConfigProvider = new CtripLocalClusterConfigProvider();
            clusterInfoProvider = new CtripLocalClusterInfoProvider();
        }
    }

    @Override
    public void setup(Set<String> names) {
        dalPropertiesManager.setup();
        dataSourceConfigureManager.setup(names, sourceType);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(name);
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(DataSourceIdentity id) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(id);
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(String name){
        Set<String> names = new HashSet<>();
        names.add(name);
        dataSourceConfigureManager.setup(names, sourceType);
        return getDataSourceConfigure(name);
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(DataSourceIdentity id) {
        Set<String> names = new HashSet<>();
        dataSourceConfigureManager.setup(names, sourceType);
        return getDataSourceConfigure(id);
    }

    @Override
    public void register(String name, DataSourceConfigureChangeListener listener) {
        register(new DataSourceName(name), listener);
    }

    @Override
    public void register(DataSourceIdentity id, DataSourceConfigureChangeListener listener) {
        dataSourceConfigureManager.register(id, listener);
    }

    @Override
    public void unregister(String name) {
        unregister(new DataSourceName(name));
    }

    @Override
    public void unregister(DataSourceIdentity id) {
        dataSourceConfigureManager.unregister(id);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        return clusterConfigProvider.getClusterConfig(clusterName);
    }

    private void setSourceType(Map<String, String> settings) {
        if (settings == null || settings.isEmpty())
            return;

        if (settings.containsKey(USE_LOCAL_CONFIG)) {
            boolean result = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
            if (result) {
                sourceType = SourceType.Local;
                return;
            }
        }

        setSourceTypeByEnv();
    }

    public void setSourceTypeByEnv() {
        Env env = Foundation.server().getEnv();
        if (env.equals(Env.UNKNOWN) || env.equals(Env.DEV) || env.equals(Env.LOCAL)) {
            sourceType = SourceType.Local;
        }
    }

    // for unit test only
    public void clear() {
        dataSourceConfigureManager.clear();
    }

    public ClusterInfo tryGetClusterInfo(String titanKey) {
        return clusterInfoProvider.getClusterInfo(titanKey);
    }

}
