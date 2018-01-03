package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

public interface PoolPropertiesProvider {
    void refreshPoolProperties();

    DataSourceConfigure mergeDataSourceConfigure(DataSourceConfigure configure);

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);
}
