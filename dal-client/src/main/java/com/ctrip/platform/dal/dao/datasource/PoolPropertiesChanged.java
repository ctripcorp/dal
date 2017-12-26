package com.ctrip.platform.dal.dao.datasource;

import java.util.Map;

public interface PoolPropertiesChanged {
    void onChanged(Map<String, String> map);
}
