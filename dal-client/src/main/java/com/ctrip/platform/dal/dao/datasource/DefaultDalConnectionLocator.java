package com.ctrip.platform.dal.dao.datasource;

import java.sql.Connection;
import java.util.*;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.*;
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
                    if (!(db instanceof ProviderDataBase)) {
                        keyNames.add(db.getConnectionString());
                    }
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
    public Connection getConnection(DataSourceIdentity id) throws Exception {
        DataSource dataSource = locator.getDataSource(id);
        return dataSource.getConnection();
    }

    @Override
    public ClusterConfigProvider getClusterConfigProvider() {
        return provider;
    }

    @Override
    public void setupCluster(Cluster cluster) {
        List<Database> databases = cluster.getDatabases();
        for (Database database : databases) {
            locator.getDataSource(new ClusterDataSourceIdentity(database));
        }
    }

    @Override
    public void uninstallCluster(Cluster cluster) {
        List<Database> databases = cluster.getDatabases();
        for (Database database : databases) {
            locator.removeDataSource(new ClusterDataSourceIdentity(database));
        }
    }

}
