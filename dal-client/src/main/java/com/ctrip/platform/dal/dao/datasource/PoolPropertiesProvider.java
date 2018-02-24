package com.ctrip.platform.dal.dao.datasource;

import java.util.Map;

public interface PoolPropertiesProvider {
    Map<String, String> getPoolProperties();

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);
}
