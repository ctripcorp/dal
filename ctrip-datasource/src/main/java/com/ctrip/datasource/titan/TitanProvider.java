package com.ctrip.datasource.titan;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.configure.dalproperties.DalPropertiesManager;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;

public class TitanProvider implements IntegratedConfigProvider {

    private static final String USE_LOCAL_CONFIG = "useLocalConfig";
    private DataSourceConfigureManager dataSourceConfigureManager = DataSourceConfigureManager.getInstance();
    private SourceType sourceType = SourceType.Remote;
    private DalPropertiesManager dalPropertiesManager = DalPropertiesManager.getInstance();

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        setSourceType(settings);
        dataSourceConfigureManager.initialize(settings);
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
        return null;
    }

    @Override
    public DataSourceConfigure forceLoadDataSourceConfigure(String name){
        Set<String> names=new HashSet<>();
        names.add(name);
        dataSourceConfigureManager.setup(names, sourceType);
        return getDataSourceConfigure(name);
    }

    @Override
    public void register(String name, DataSourceConfigureChangeListener listener) {
        dataSourceConfigureManager.register(name, listener);
    }

    @Override
    public ClusterConfig getClusterConfig(String clusterName) {
        return null;
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

}
