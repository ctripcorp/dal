package com.ctrip.platform.dal.dao.datasource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.platform.dal.dao.client.ConnectionAction;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.helper.ConnectionStringKeyHelper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;

public class DefaultDalConnectionLocator extends InjectableComponentSupport implements DalConnectionLocator {

    public static final String DATASOURCE_CONFIG_PROVIDER = "dataSourceConfigureProvider";

    private DataSourceLocator locator;
    private IntegratedConfigProvider provider;

    @Override
    protected void pInitialize(Map<String, String> settings) throws Exception {
        provider = new DefaultDataSourceConfigureProvider();
        if (settings.containsKey(DATASOURCE_CONFIG_PROVIDER)) {
            provider = (IntegratedConfigProvider) Class.forName(settings.get(DATASOURCE_CONFIG_PROVIDER)).newInstance();
        }

        if (provider instanceof InjectableComponentSupport)
            ((InjectableComponentSupport) provider).inject(getDatabaseSets());
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
        return getConnection(name, null);
    }

    @Override
    public Connection getConnection(String name, ConnectionAction action) throws Exception {
        String keyName = ConnectionStringKeyHelper.getKeyName(name);
        DataSource dataSource = locator.getDataSource(keyName);
        logConnectionUrl(dataSource, action);
        return dataSource.getConnection();
    }

    // in case we can't log sql.database if there is exception while create a connection
    private void logConnectionUrl(DataSource dataSource, ConnectionAction action) {
        try{
            if (dataSource instanceof SingleDataSourceWrapper) {
                SingleDataSource singleDataSource = ((SingleDataSourceWrapper) dataSource).getSingleDataSource();
                if (singleDataSource != null) {
                    action.entry.setDbUrl(singleDataSource.getDataSourceConfigure().getConnectionUrl());
                }
            }
        } catch (Throwable t) {
            // no need to do
        }
    }

    @Override
    public Connection getConnection(DataSourceIdentity id) throws Exception {
        return getConnection(id, null);
    }

    @Override
    public Connection getConnection(DataSourceIdentity id, ConnectionAction action) throws Exception {
        DataSource dataSource = locator.getDataSource(id);
        logConnectionUrl(dataSource, action);
        return dataSource.getConnection();
    }

    @Override
    public IntegratedConfigProvider getIntegratedConfigProvider() {
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
