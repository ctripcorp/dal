package com.ctrip.platform.dal.dao.configure;

public interface DalConnectionStringConfigure extends ConnectionStringConfigure {
    String getName();

    String getVersion();

    String getHostName();
}
