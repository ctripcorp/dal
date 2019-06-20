package com.ctrip.platform.dal.dao.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ctrip.platform.dal.dao.configure.SingleDataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.IDataSourceConfigure;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;

public class DataSourceLocator {
    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private static final ConcurrentHashMap<String, DataSource> cache = new ConcurrentHashMap<>();

    private DatasourceBackgroundExecutor executor = DalElementFactory.DEFAULT.getDatasourceBackgroundExecutor();

    private DataSourceConfigureProvider provider;

    public DataSourceLocator(DataSourceConfigureProvider provider) {
        this.provider = provider;
    }

    // to be refactored
    public static boolean containsKey(String name) {
        return cache.containsKey(name);
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
        DataSource ds = cache.get(name);

        if (ds != null) {
            return ds;
        }

        synchronized (this.getClass()) {
            ds = cache.get(name);
            if (ds != null) {
                return ds;
            }
            try {
                ds = createDataSource(name);
                cache.put(name, ds);
            } catch (Throwable e) {
                String msg = "Creating DataSource " + name + " error:" + e.getMessage();
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }

        return ds;
    }

    private DataSource createDataSource(String name) throws SQLException {
        IDataSourceConfigure config = provider.getDataSourceConfigure(name);
        if (config == null) {
            throw new SQLException("Can not find connection configure for " + name);
        }

        SingleDataSourceConfigureProvider dataSourceConfigureProvider = new SingleDataSourceConfigureProvider(name, provider);
        ForceSwitchableDataSource fsds = new ForceSwitchableDataSource(name, dataSourceConfigureProvider);
        provider.register(name, fsds);
        executor.execute(fsds);

        return fsds;
    }

    public static Map<String, Integer> getActiveConnectionNumber() {
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, DataSource> entry : cache.entrySet()) {
            DataSource dataSource = entry.getValue();
            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                org.apache.tomcat.jdbc.pool.DataSource ds = (org.apache.tomcat.jdbc.pool.DataSource) dataSource;
                map.put(entry.getKey(), ds.getActive());
            }
        }

        return map;
    }

}
