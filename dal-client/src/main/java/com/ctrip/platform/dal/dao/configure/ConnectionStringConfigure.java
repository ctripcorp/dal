package com.ctrip.platform.dal.dao.configure;

public interface ConnectionStringConfigure {
    String getName();

    String getUserName();

    String getPassword();

    String getConnectionUrl();

    String getDriverClass();

    String getVersion();
}
