package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;

public interface PoolPropertiesProvider {
    PoolPropertiesConfigure getPoolProperties();

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);
}
