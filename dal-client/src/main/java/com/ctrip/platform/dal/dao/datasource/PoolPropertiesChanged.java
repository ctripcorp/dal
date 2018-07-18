package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.PoolPropertiesConfigure;

public interface PoolPropertiesChanged {
    void onChanged(PoolPropertiesConfigure configure);
}
