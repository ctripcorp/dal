package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

import java.util.Map;

public interface PoolPropertiesProvider {
    void initializePoolProperties();

    void setPoolProperties(Map<String, String> map);

    DataSourceConfigure mergeDataSourceConfigure(DataSourceConfigure configure);

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);
}
