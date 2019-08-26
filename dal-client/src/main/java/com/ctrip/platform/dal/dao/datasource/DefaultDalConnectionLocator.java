package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.ClusterDatabaseSet;
import com.ctrip.platform.dal.dao.configure.DataBase;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.IntegratedConfigProvider;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;

import javax.sql.DataSource;

public class DefaultDalConnectionLocator implements DalConnectionLocator {

    public static final String DATASOURCE_CONFIG_PROVIDER = "dataSourceConfigureProvider";

    private DataSourceLocator locator;
    private IntegratedConfigProvider provider;

    @Override
    public void initialize(Map<String, String> settings) throws Exception {
        provider = new DefaultDataSourceConfigureProvider();
        if (settings.containsKey(DATASOURCE_CONFIG_PROVIDER)) {
            provider = (IntegratedConfigProvider) Class.forName(settings.get(DATASOURCE_CONFIG_PROVIDER)).newInstance();
        }

        provider.initialize(settings);

        locator = new DataSourceLocator(provider);
    }

    @Override
    public void setup(Collection<DatabaseSet> databaseSets) {
        Set<String> keyNames = new HashSet<>();
        for (DatabaseSet dbSet : databaseSets) {
            if (dbSet instanceof ClusterDatabaseSet) {
                locator.setup(((ClusterDatabaseSet) dbSet).getCluster());
            } else  {
                for (DataBase db : dbSet.getDatabases().values()) {
                    keyNames.add(db.getConnectionString());
                }
            }
        }
        provider.setup(keyNames);
    }

    @Override
    public Connection getConnection(String name) throws Exception {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        DataSource dataSource = locator.getDataSource(keyName);
        return dataSource.getConnection();
    }

    @Override
    public Connection getConnection(Database database) throws Exception {
        DataSourceIdentity id = new ClusterDataSourceIdentity(database);
        DataSource dataSource = locator.getDataSource(id);
        return dataSource.getConnection();
    }

    @Override
    public ClusterConfigProvider getClusterConfigProvider() {
        return provider;
    }

}
