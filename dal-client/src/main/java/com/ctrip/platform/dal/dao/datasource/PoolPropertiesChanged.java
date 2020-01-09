package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DalPoolPropertiesConfigure;

public interface PoolPropertiesChanged {
    void onChanged(DalPoolPropertiesConfigure configure);
}
