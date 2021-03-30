package com.ctrip.platform.dal.dao.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.cluster.DrcCluster;
import com.ctrip.framework.dal.cluster.client.config.LocalizationConfig;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.dao.cluster.ClusterManager;
import com.ctrip.platform.dal.dao.cluster.ClusterManagerImpl;
import com.ctrip.platform.dal.dao.cluster.DynamicCluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.cluster.ClusterDataSource;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

public class DataSourceLocator {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();
    private static final String LOG_TYPE_CREATE_DATASOURCE = "DAL.dataSource";
    private static final String LOG_NAME_CREATE_DRC_DATASOURCE = "createDrcDataSource:%s";
    private static final String LOG_NAME_CREATE_DRC_DATASOURCE_FAIL = "createDrcDataSource:EXCEPTION:%s";
    private static final String LOG_NAME_CREATE_NORMAL_DATASOURCE = "createNormalDataSource:%s";
    private static final String LOG_NAME_CREATE_CLUSTER_DATASOURCE = "createClusterDataSource:%s";

    private static final Map<DataSourceIdentity, DataSource> cache = new ConcurrentHashMap<>();

    private DatasourceBackgroundExecutor executor = DalElementFactory.DEFAULT.getDatasourceBackgroundExecutor();
    private IntegratedConfigProvider provider;
    private ClusterManager clusterManager;
    private LocalizationValidatorFactory factory = DalElementFactory.DEFAULT.getLocalizationValidatorFactory();

    private boolean isForceInitialize = false;

    public DataSourceLocator(IntegratedConfigProvider provider) {
        this(provider, false);
    }

    public DataSourceLocator(IntegratedConfigProvider provider, boolean isForceInitialize) {
        this.provider = provider;
        this.isForceInitialize = isForceInitialize;
        this.clusterManager = new ClusterManagerImpl(provider);
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
     */
    public DataSource getDataSource(String name) {
        return getDataSource(new DataSourceName(name));
    }

    public DataSource getDataSource(DataSourceIdentity id) {
        DataSource ds = cache.get(id);
        if (ds == null) {
            synchronized (cache) {
                ds = cache.get(id);
                if (ds == null) {
                    try {
                        if (id instanceof ClusterInfoDelegateIdentity) {
                            ClusterInfo clusterInfo = ((ClusterInfoDelegateIdentity) id).getClusterInfo();
                            Cluster cluster = clusterInfo.getCluster();
                            if (cluster == null)
                                throw new RuntimeException("Cluster not created");
                            ds = createDataSource(id, clusterInfo, cluster);
                        } else
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

    public DataSource getDataSource(ClusterInfo clusterInfo) {
        // todo-lhj 为什么根据clusterInfo还要重新创建一遍cluster
        Cluster cluster = clusterManager.getOrCreateCluster(clusterInfo.getClusterName(), null);
        clusterInfo.setCluster(cluster);
        DataSourceIdentity id = clusterInfo.toDataSourceIdentity();
        DataSource ds = cache.get(id);
        if (ds == null) {
            synchronized (cache) {
                ds = cache.get(id);
                if (ds == null) {
                    try {
                        ds = createDataSource(id, clusterInfo, cluster);
                        cache.put(id, ds);
                    } catch (Throwable t) {
                        String msg = String.format("error when creating cluster datasource: %s", id.getId());
                        LOGGER.error(msg, t);
                        throw new RuntimeException(msg, t);
                    }
                }
            }
        }
        return ds;
    }

    public void removeDataSource(DataSourceIdentity id) {
        DataSource ds = cache.remove(id);
        provider.unregister(id);
        if (ds instanceof ClosableDataSource) {
            ((ClosableDataSource) ds).close();
        }
    }

    private DataSource createDataSource(DataSourceIdentity id) throws SQLException {
        DataSourceConfigure config = provider.getDataSourceConfigure(id);
        if (config == null && !isForceInitialize) {
            throw new SQLException(String.format("datasource configure not found for %s", id.getId()));
        }
        RefreshableDataSource ds = createRefreshableDataSource(id);
        provider.register(id, ds);
        executor.execute(ds);
        return ds;
    }

    private RefreshableDataSource createRefreshableDataSource(DataSourceIdentity id) throws SQLException {
        if (id instanceof ClusterDataSourceIdentity) {
            Database database = ((ClusterDataSourceIdentity) id).getDatabase();
            Cluster cluster = database.getCluster();
            ClusterInfo clusterInfo = new ClusterInfo(database.getClusterName(), database.getShardIndex(),
                    database.isMaster() ? DatabaseRole.MASTER : DatabaseRole.SLAVE,
                    cluster != null && cluster.dbShardingEnabled(), cluster);
            try {
                if (cluster != null && cluster.getClusterType() == ClusterType.DRC) {
                    LocalizationConfig localizationConfig = cluster.getLocalizationConfig();
                    LocalizationConfig lastLocalizationConfig = cluster.getLastLocalizationConfig();
                    LocalizationValidator validator = factory.createValidator(clusterInfo, localizationConfig, lastLocalizationConfig);
                    LOGGER.logEvent(LOG_TYPE_CREATE_DATASOURCE, String.format(LOG_NAME_CREATE_DRC_DATASOURCE,
                            clusterInfo.toString()), localizationConfig.toString());
                    return new LocalizedDataSource(validator, id, provider.getDataSourceConfigure(id));
                }
            } catch (Exception e) {
                LOGGER.logEvent(LOG_TYPE_CREATE_DATASOURCE, String.format(LOG_NAME_CREATE_DRC_DATASOURCE_FAIL,
                        clusterInfo.toString()), e.getMessage());
                throw e;
            }
        }
        LOGGER.logEvent(LOG_TYPE_CREATE_DATASOURCE, String.format(LOG_NAME_CREATE_NORMAL_DATASOURCE, id.getId()), "");
        SingleDataSourceConfigureProvider dataSourceConfigureProvider = new SingleDataSourceConfigureProvider(id, provider);
        return new ForceSwitchableDataSource(id, dataSourceConfigureProvider);
    }

    private DataSource createDataSource(DataSourceIdentity id, ClusterInfo clusterInfo, Cluster cluster) throws SQLException {
        ClusterDynamicDataSource ds = new ClusterDynamicDataSource(clusterInfo, cluster, provider, factory);
        LOGGER.logEvent(LOG_TYPE_CREATE_DATASOURCE, String.format(LOG_NAME_CREATE_CLUSTER_DATASOURCE, id.getId()), "");
        provider.register(id, ds);
        executor.execute(ds);
        return ds;
    }

    public void setup(Cluster cluster) {}

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
