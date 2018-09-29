package com.ctrip.platform.dal.dao.configure;


import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

/**
 * Created by lilj on 2018/9/23.
 */
public class InvalidConnectionString implements DalInvalidConnectionString {
    private DalException connectionStringException;
    private String name;

    public InvalidConnectionString(String name, DalException connectionStringException) {
        this.name = name;
        this.connectionStringException = connectionStringException;
    }

    public DalException getConnectionStringException() {
        return connectionStringException;
    }

    public String getName() {
        return name;
    }

    public String getIPConnectionString() {
        throw new DalRuntimeException(String.format("The IPConnectionString of %s is null", name));
    }

    public String getDomainConnectionString() {
        throw new DalRuntimeException(String.format("The DomainConnectionString of %s is null", name));
    }

    public ConnectionStringConfigure getIPConnectionStringConfigure() {
        throw new DalRuntimeException(String.format("The IPConnectionStringConfigure of %s is null", name));
    }

    public ConnectionStringConfigure getDomainConnectionStringConfigure() {
        throw new DalRuntimeException(String.format("The DomainConnectionStringConfigure of %s is null", name));
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof DalInvalidConnectionString))
            return false;
        InvalidConnectionString connectionString = (InvalidConnectionString) o;
        return name.equals(connectionString.getName())
                && connectionStringException.equals(connectionString.getConnectionStringException());
    }
}
