package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.database.ConnectionString;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.configure.DalConnectionString;
import com.ctrip.platform.dal.dao.configure.DalConnectionStringConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;

/**
 * @author c7ch23en
 */
public class ClusterDataSourceIdentity implements DataSourceIdentity {

    private static final String ID_FORMAT = "";

    private Database database;
    private DalConnectionString connectionString;

    public ClusterDataSourceIdentity(Database database) {
        this.database = database;
        this.connectionString = new ClusterConnectionStringImpl(getId(), database);
    }

    @Override
    public String getId() {
        return null;
    }

    public DalConnectionString getConnectionString() {
        return connectionString;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private static class ClusterConnectionStringImpl implements DalConnectionString {

        private String name;
        private Database database;

        public ClusterConnectionStringImpl(String name, Database database) {
            this.name = name;
            this.database = database;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getIPConnectionString() {
            return database.getConnectionString().getPrimaryConnectionUrl();
        }

        @Override
        public String getDomainConnectionString() {
            return database.getConnectionString().getFailOverConnectionUrl();
        }

        @Override
        public DalConnectionStringConfigure getIPConnectionStringConfigure() {
            DataSourceConfigure configure = new DataSourceConfigure(getName());
            ConnectionString connectionString = database.getConnectionString();
            configure.setConnectionUrl(connectionString.getPrimaryConnectionUrl());
            configure.setUserName(connectionString.getUsername());
            configure.setPassword(connectionString.getPassword());
            configure.setDriverClass(connectionString.getDriverClassName());
            return configure;
        }

        @Override
        public DalConnectionStringConfigure getDomainConnectionStringConfigure() {
            DataSourceConfigure configure = new DataSourceConfigure(getName());
            ConnectionString connectionString = database.getConnectionString();
            configure.setConnectionUrl(connectionString.getFailOverConnectionUrl());
            configure.setUserName(connectionString.getUsername());
            configure.setPassword(connectionString.getPassword());
            configure.setDriverClass(connectionString.getDriverClassName());
            return configure;
        }

        @Override
        public DalConnectionString clone() {
            throw new UnsupportedOperationException("clone not supported");
        }

    }

}
