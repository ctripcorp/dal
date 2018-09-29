package com.ctrip.platform.dal.dao.datasource;


import com.ctrip.platform.dal.dao.configure.DalConnectionString;

public interface ConnectionStringChanged {
    void onChanged(DalConnectionString connectionString);
}
