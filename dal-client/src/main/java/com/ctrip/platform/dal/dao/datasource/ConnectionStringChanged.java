package com.ctrip.platform.dal.dao.datasource;

import java.util.Map;

public interface ConnectionStringChanged {
    void onChanged(Map<String, String> map);
}
