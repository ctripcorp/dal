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

    private static final String ID_FORMAT = "%s-%d-%s-%s"; // cluster-shard-role-host
    private static final String MASTER = "master";
    private static final String SLAVE = "slave";

    private Database database;
    private String id;
    private DalConnectionString connectionString;

    public ClusterDataSourceIdentity(Database database) {
        this.database = database;
        init();
    }

    private void init() {
        String role = database.isMaster() ? MASTER : SLAVE;
        ConnectionString connString = database.getConnectionString();
        id = String.format(ID_FORMAT, database.getClusterName(), database.getShardIndex(), role,
                connString.getPrimaryHost());
        this.connectionString = new ClusterConnectionStringImpl(id, database);
    }

    @Override
    public String getId() {
        return id;
    }

    public DalConnectionString getDalConnectionString() {
        return connectionString;
    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClusterDataSourceIdentity) {
            Database objDatabase = ((ClusterDataSourceIdentity) obj).getDatabase();
            return (database != null && database.equals(objDatabase)) || (database == null && objDatabase == null);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return database != null ? database.hashCode() : 0;
    }

    public static class ClusterConnectionStringImpl implements DalConnectionString {

        private String name;
        private Database database;
        private ConnectionString connectionString;
        private String[] aliasKeys;

        public ClusterConnectionStringImpl(String name, Database database) {
            this.name = name;
            this.database = database;
            this.connectionString = database.getConnectionString();
            this.aliasKeys = database.getAliasKeys();
        }

        @Override
        public String getName() {
            return name;
        }

        public String[] getAliasKeys() {
            return aliasKeys;
        }

        @Override
        public String getIPConnectionString() {
            return connectionString.getPrimaryConnectionUrl();
        }

        @Override
        public String getDomainConnectionString() {
            return connectionString.getFailOverConnectionUrl();
        }

        @Override
        public DalConnectionStringConfigure getIPConnectionStringConfigure() {
            DataSourceConfigure configure = new DataSourceConfigure(name);
            configure.setConnectionUrl(connectionString.getPrimaryConnectionUrl());
            configure.setUserName(connectionString.getUsername());
            configure.setPassword(connectionString.getPassword());
            configure.setDriverClass(connectionString.getDriverClassName());
            return configure;
        }

        @Override
        public DalConnectionStringConfigure getDomainConnectionStringConfigure() {
            DataSourceConfigure configure = new DataSourceConfigure(name);
            configure.setConnectionUrl(connectionString.getFailOverConnectionUrl());
            configure.setUserName(connectionString.getUsername());
            configure.setPassword(connectionString.getPassword());
            configure.setDriverClass(connectionString.getDriverClassName());
            return configure;
        }

        @Override
        public DalConnectionString clone() {
            return new ClusterConnectionStringImpl(name, database);
        }

    }

}
