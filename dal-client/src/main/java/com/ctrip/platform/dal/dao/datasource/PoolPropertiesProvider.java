package com.ctrip.platform.dal.dao.datasource;

import java.util.Map;

public interface PoolPropertiesProvider {
    void initializePoolProperties();

    void setPoolProperties(Map<String, String> map);

    void addPoolPropertiesChangedListener(final PoolPropertiesChanged callback);

}
