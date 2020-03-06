package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;

public interface ConnectionStringConfigureChanged {
    void onChanged(DalConnectionStringConfigure connectionStringConfigure);
}
