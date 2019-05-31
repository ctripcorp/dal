package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DalPoolPropertiesConfigure;

public interface PoolPropertiesProvider {
    DalPoolPropertiesConfigure getPoolProperties();

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);
}
