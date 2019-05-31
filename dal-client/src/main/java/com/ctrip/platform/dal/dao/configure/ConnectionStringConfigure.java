package com.ctrip.platform.dal.dao.configure;

public interface ConnectionStringConfigure {

    String getUserName();

    String getPassword();

    String getConnectionUrl();

    String getDriverClass();

    String getHostName();

    Integer getPort();

    String getDBName();

//    characterEncoding, UTF-8 default
    String getEncoding();
}
