package com.ctrip.platform.dal.dao.datasource;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.platform.dal.dao.configure.DataSourceConfigure;
import com.ctrip.platform.dal.dao.configure.DataSourceConfigureProvider;
import com.ctrip.platform.dal.dao.configure.DefaultDataSourceConfigureProvider;

public class DataSourceLocator {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceLocator.class);

    private static final ConcurrentHashMap<String, DataSource> cache = new ConcurrentHashMap<String, DataSource>();

    private static final Object LOCK = new Object();
    private static final Object LOCK2 = new Object();
    private static final String SEMICOLON = ";";
    private static final String AT = "@";

    private static ConcurrentHashMap<String, ConcurrentHashMap<String, PoolProperties>> poolPropertiesMap =
            new ConcurrentHashMap<>();

    private DataSourceConfigureProvider provider;

    public DataSourceLocator(DataSourceConfigureProvider provider) {
        this.provider = provider;
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
                logger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
        return ds;
    }

    private DataSource createDataSource(String name) throws SQLException {
//        DatabasePoolConfig poolConfig = DatabasePoolConfigParser.getInstance().getDatabasePoolConifg(name);
        
        DataSourceConfigure config = provider.getDataSourceConfigure(name);
        if (config == null) {
            throw new SQLException("Can not find connection configure for " + name);
        }
        
        RefreshableDataSource rds = new RefreshableDataSource(name, config);
        provider.register(name, rds);
        
        return rds;
    }

    static void setPoolProperties(PoolProperties poolProperties) {
        if (poolProperties == null)
            return;

        String url = poolProperties.getUrl();
        if (url == null || url.length() == 0)
            return;

        String userName = poolProperties.getUsername();
        if (userName == null || userName.length() == 0)
            return;

        url = getShortString(url, SEMICOLON);
        userName = getShortString(userName, AT);
        ConcurrentHashMap<String, PoolProperties> map = poolPropertiesMap.get(url);

        if (map == null) {
            synchronized (LOCK) {
                map = poolPropertiesMap.get(url);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    poolPropertiesMap.put(url, map);
                }
            }
        }

        if (!map.containsKey(userName)) {
            synchronized (LOCK2) {
                if (!map.containsKey(userName)) {
                    map.put(userName, poolProperties);
                }
            }
        }
    }

    public static PoolProperties getPoolProperties(String url, String userName) {
        if (url == null || url.length() == 0)
            return null;
        if (userName == null || userName.length() == 0)
            return null;

        url = getShortString(url, SEMICOLON);
        userName = getShortString(userName, AT);
        ConcurrentHashMap<String, PoolProperties> map = poolPropertiesMap.get(url);
        if (map == null)
            return null;
        return map.get(userName);
    }

    private static String getShortString(String str, String separator) {
        if (str == null || str.length() == 0)
            return null;
        int index = str.indexOf(separator);
        if (index > -1)
            str = str.substring(0, index);
        return str;
    }

}
