package com.ctrip.platform.dal.dao.configure;

import com.ctrip.platform.dal.exceptions.DalException;

public class InvalidVariableConnectionString implements DalConnectionStringConfigure {

    private DalException connectionStringException;
    private String name;

    public InvalidVariableConnectionString(String name, DalException connectionStringException) {
        this.name = name;
        this.connectionStringException = connectionStringException;
    }

    public DalException getConnectionStringException() {
        return connectionStringException;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getHostName() {
        return null;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getConnectionUrl() {
        return null;
    }

    @Override
    public String getDriverClass() {
        return null;
    }
}
