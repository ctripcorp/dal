package com.ctrip.datasource.titan;

import java.util.Map;
import java.util.Set;

import com.ctrip.datasource.configure.DalPropertiesManager;
import com.ctrip.framework.foundation.Env;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.datasource.common.enums.SourceType;
import com.ctrip.platform.dal.dao.configure.*;

public class TitanProvider implements DataSourceConfigureProvider {
    private static final String USE_LOCAL_CONFIG = "useLocalConfig";
    private DataSourceConfigureManager dataSourceConfigureManager = DataSourceConfigureManager.getInstance();
    private SourceType sourceType = SourceType.Remote;
    private DalPropertiesManager dalSettingsManager = DalPropertiesManager.getInstance();

    public void initialize(Map<String, String> settings) throws Exception {
        setSourceType(settings);
        dataSourceConfigureManager.initialize(settings);
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
        if (env.equals(Env.UNKNOWN) || env.equals(Env.DEV)) {
            sourceType = SourceType.Local;
        }
    }

    @Override
    public DataSourceConfigure getDataSourceConfigure(String name) {
        return DataSourceConfigureLocatorManager.getInstance().getDataSourceConfigure(name);
    }

    @Override
    public void setup(Set<String> names) {
        dalSettingsManager.setup();
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
