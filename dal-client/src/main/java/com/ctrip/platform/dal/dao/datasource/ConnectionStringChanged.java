package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.ConnectionString;

public interface ConnectionStringChanged {
    void onChanged(ConnectionString connectionString);
}
