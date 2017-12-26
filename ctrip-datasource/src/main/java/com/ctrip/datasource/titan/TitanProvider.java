package com.ctrip.datasource.titan;

import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureChangeListener;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;

public class TitanProvider implements DataSourceConfigureProvider {
    private static final String USE_LOCAL_CONFIG = "useLocalConfig";
    private DataSourceConfigureManager dataSourceConfigureManager = DataSourceConfigureManager.getInstance();
    private SourceType sourceType = SourceType.Remote;

    public void initialize(Map<String, String> settings) throws Exception {
        getSourceType(settings);
        dataSourceConfigureManager.initialize(settings);
    }

    private void getSourceType(Map<String, String> settings) {
        if (settings == null || settings.isEmpty())
            return;

        if (settings.containsKey(USE_LOCAL_CONFIG)) {
            boolean result = Boolean.parseBoolean(settings.get(USE_LOCAL_CONFIG));
            if (result)
                sourceType = SourceType.Local;
        }
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        return dataSourceConfigureManager.getDataSourceConfigure(name);
    }

    @Override
    public void setup(Set<String> names) {
        dataSourceConfigureManager.setup(names, sourceType);
    }

    @Override
    public void register(String name, DataSourceConfigureChangeListener listener) {
        dataSourceConfigureManager.register(name, listener);
    }

    // for unit test only
    public void clear() {
        dataSourceConfigureManager.clear();
    }

}
