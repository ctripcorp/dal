package com.ctrip.platform.dal.dao.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

public class DataSourceLocator {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final Map<DataSourceIdentity, DataSource> cache = new ConcurrentHashMap<>();

    private DatasourceBackgroundExecutor executor = DalElementFactory.DEFAULT.getDatasourceBackgroundExecutor();
    private DataSourceConfigureProvider provider;

    private boolean isForceInitialize = false;

    public DataSourceLocator(DataSourceConfigureProvider provider) {
        this.provider = provider;
    }

    public DataSourceLocator(DataSourceConfigureProvider provider, boolean isForceInitialize) {
        this.provider = provider;
        this.isForceInitialize = isForceInitialize;
    }

    // to be refactored
    public static boolean containsKey(String name) {
        return cache.containsKey(new DataSourceName(name));
    }

    /**
     * This is used for initialize datasource for thirdparty framework
     */
    public DataSourceLocator() {
        this(new DefaultDataSourceConfigureProvider());
    }

    /**
     * Get DataSource by real db source name
     *
     * @param name
     * @return DataSource
     * @throws NamingException
     */
    public DataSource getDataSource(String name) throws Exception {
        return getDataSource(new DataSourceName(name));
    }

    public DataSource getDataSource(DataSourceIdentity id) {
        DataSource ds = cache.get(id);
        if (ds == null) {
            synchronized (cache) {
                ds = cache.get(id);
                if (ds == null) {
                    try {
                        ds = createDataSource(id);
                        cache.put(id, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating datasource: %s", id.getId());
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        return ds;
    }

    private DataSource createDataSource(DataSourceIdentity id) throws SQLException {
        DataSourceConfigure config = provider.getDataSourceConfigure(id);
        if (config == null && !isForceInitialize) {
            throw new SQLException(String.format("datasource configure not found for %s", id.getId()));
        }

        SingleDataSourceConfigureProvider dataSourceConfigureProvider = new SingleDataSourceConfigureProvider(id, provider);
        ForceSwitchableDataSource ds = new ForceSwitchableDataSource(id, dataSourceConfigureProvider);
        provider.register(id, ds);
        executor.execute(ds);

        return ds;
    }

    public void setup(Cluster cluster) {
    }

    public static Map<String, Integer> getActiveConnectionNumber() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<DataSourceIdentity, DataSource> entry : cache.entrySet()) {
            DataSource dataSource = entry.getValue();
            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                map.put(entry.getKey().getId(), ds.getActive());
            }
        }
        return map;
    }

}
