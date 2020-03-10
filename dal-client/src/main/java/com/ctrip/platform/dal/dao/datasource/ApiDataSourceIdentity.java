package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class ApiDataSourceIdentity implements DataSourceIdentity {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String ID_FORMAT = "%s_mysql_api"; //dbName

    private ConnectionStringConfigureProvider provider;
    private String id;
    private DalConnectionStringConfigure connectionStringConfigure;
    private DalConnectionString connectionString;

    public ApiDataSourceIdentity(ConnectionStringConfigureProvider provider) {
        this.provider = provider;
    }

    private void initConnectionString() {
        try {
            connectionStringConfigure = provider.getConnectionString();
        } catch (Exception e) {
            LOGGER.error("get connectionString from mysql api failed!", e);
            throw new DalRuntimeException(e);
        }

        id = String.format(ID_FORMAT, connectionStringConfigure.getName());

        if (connectionStringConfigure instanceof InvalidVariableConnectionString) {
            connectionString = new InvalidConnectionString(id, ((InvalidVariableConnectionString) connectionStringConfigure).getConnectionStringException());
        }
        else {
            connectionString = new MysqlApiConnectionStringImpl(connectionStringConfigure);
        }
    }

    public ConnectionStringConfigureProvider getProvider() {
        return provider;
    }

    public DalConnectionString getConnectionString() {
        initConnectionString();
        return connectionString;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ApiDataSourceIdentity) {
            ConnectionStringConfigureProvider objProvider = ((ApiDataSourceIdentity) obj).getProvider();
            return (provider != null && provider.equals(objProvider)) || (provider == null && objProvider == null);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return provider != null ? provider.hashCode() : 0;
    }

    public static class MysqlApiConnectionStringImpl implements DalConnectionString {

        private DalConnectionStringConfigure connectionStringConfigure;

        public MysqlApiConnectionStringImpl(DalConnectionStringConfigure connectionStringConfigure) {
            this.connectionStringConfigure = connectionStringConfigure;
        }

        @Override
        public String getName() {
            return connectionStringConfigure.getName();
        }

        @Override
        public String getIPConnectionString() {
            return connectionStringConfigure.getConnectionUrl();
        }

        @Override
        public String getDomainConnectionString() {
            return connectionStringConfigure.getConnectionUrl();
        }

        @Override
        public DalConnectionStringConfigure getIPConnectionStringConfigure() {
            return connectionStringConfigure;
        }

        @Override
        public DalConnectionStringConfigure getDomainConnectionStringConfigure() {
            return connectionStringConfigure;
        }

        @Override
        public DalConnectionString clone() {
            throw new UnsupportedOperationException("clone not supported");
        }
    }

}
