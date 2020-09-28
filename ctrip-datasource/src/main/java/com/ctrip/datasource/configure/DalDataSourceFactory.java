package com.ctrip.datasource.configure;

import java.util.*;

import javax.sql.DataSource;

import com.ctrip.datasource.titan.TitanDataSourceLocator;
import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.cluster.ClusterManager;
import com.ctrip.platform.dal.dao.cluster.ClusterManagerImpl;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.dao.configure.ClusterInfo;
import com.ctrip.platform.dal.dao.configure.IntegratedConfigProvider;
import com.ctrip.platform.dal.dao.datasource.*;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

public class DalDataSourceFactory {
    private static final String IGNORE_EXTERNAL_EXCEPTION = "ignoreExternalException";

    /**
     * Create DataSource for given name. The appid and titan url will be discoved by framework foundation
     * 
     * @param allInOneKey
     * @return DataSource
     * @throws Exception
     */
    public DataSource createDataSource(String allInOneKey) throws Exception {
        return createDataSource(allInOneKey, null, null);
    }

    public DataSource createDataSource(String allInOneKey, boolean isForceInitialize) throws Exception {
        return createDataSource(allInOneKey, null, null, isForceInitialize);
    }

    /**
     * Create DataSource for given name. In case user has clog or cat configured. The name will be same for both PROD
     * and DEV environment
     * 
     * @param allInOneKey
     * @param svcUrl
     * @return DataSource
     * @throws Exception
     */
    public DataSource createDataSource(String allInOneKey, String svcUrl) throws Exception {
        return createDataSource(allInOneKey, svcUrl, null);
    }

    /**
     * Create DataSource for given name. In case user has no clog or cat configured. The name will be same for both PROD
     * and DEV environment
     * 
     * @param allInOneKey
     * @param svcUrl
     * @param appid
     * @return DataSource
     * @throws Exception
     */
    public DataSource createDataSource(String allInOneKey, String svcUrl, String appid) throws Exception {
        return createDataSource(allInOneKey, svcUrl, appid, false);
    }

    /**
     * support create mgr or standalone datasource
     *
     * @param dbName
     * @return DataSource
     * @throws Exception
     */
    public DataSource createVariableTypeDataSource(String dbName) throws Exception {
        return createVariableTypeDataSource(new MysqlApiConnectionStringConfigureProvider(dbName), false);
    }

    /**
     * support create mgr or standalone datasource
     *
     * @param dbName
     * @param isForceInitialize
     * @return DataSource
     * @throws Exception
     */
    public DataSource createVariableTypeDataSource(String dbName, boolean isForceInitialize) throws Exception {
        return createVariableTypeDataSource(new MysqlApiConnectionStringConfigureProvider(dbName), isForceInitialize);
    }

    /**
     * support create mgr or standalone datasource
     *
     * @param connectionStringConfigureProvider
     * @return DataSource
     * @throws Exception
     */
    public DataSource createVariableTypeDataSource(ConnectionStringConfigureProvider connectionStringConfigureProvider) throws Exception {
        return createVariableTypeDataSource(connectionStringConfigureProvider, false);
    }

    /**
     * support create mgr or standalone datasource
     *
     * @param connectionStringConfigureProvider
     * @return DataSource
     * @throws Exception
     */
    public DataSource createVariableTypeDataSource(ConnectionStringConfigureProvider connectionStringConfigureProvider, boolean isForceInitialize) throws Exception {
        TitanProvider provider = initTitanProvider(isForceInitialize);

        Set<String> names = new HashSet<>();
        provider.setup(names);

        DataSourceIdentity id = new ApiDataSourceIdentity(connectionStringConfigureProvider);
        DataSourceLocator locator = new DataSourceLocator(provider, isForceInitialize);
        return locator.getDataSource(id);
    }

    public DataSource createDataSource(String allInOneKey, String svcUrl, String appid, boolean isForceInitialize) throws Exception {
        TitanProvider provider = initTitanProvider(isForceInitialize);

        Set<String> names = new HashSet<>();
        ClusterInfo clusterInfo = provider.tryGetClusterInfo(allInOneKey);

        if (clusterInfo == null || clusterInfo.getRole() != DatabaseRole.MASTER)
            names.add(allInOneKey);

        provider.setup(names);
        DataSourceLocator locator = new DataSourceLocator(provider, isForceInitialize);

        if (clusterInfo == null || clusterInfo.getRole() != DatabaseRole.MASTER)
            return locator.getDataSource(new TraceableDataSourceName(allInOneKey));
        else
            return locator.getDataSource(clusterInfo);
    }

    private TitanProvider initTitanProvider(boolean isForceInitialize) throws Exception {
        TitanProvider provider = new TitanProvider();
        Map<String, String> settings = new HashMap<>();
        settings.put(IGNORE_EXTERNAL_EXCEPTION, String.valueOf(isForceInitialize));
        provider.initialize(settings);
        return provider;
    }

    /**
     * Get or create master DataSource for non-sharding dal cluster
     *
     * @param clusterName dal cluster name
     * @return DataSource
     * @throws Exception
     */
    public DataSource getOrCreateDataSource(String clusterName) throws Exception {
        return getOrCreateDataSource(clusterName, null, DatabaseRole.MASTER);
    }

    /**
     * Get or create master DataSource for dal cluster on specified shard
     *
     * @param clusterName dal cluster name
     * @param shardIndex shard index
     * @return DataSource
     * @throws Exception
     */
    public DataSource getOrCreateDataSource(String clusterName, Integer shardIndex) throws Exception {
        return getOrCreateDataSource(clusterName, shardIndex, DatabaseRole.MASTER);
    }

    /**
     * Get or create DataSource with specified role for non-sharding dal cluster
     *
     * @param clusterName dal cluster name
     * @param databaseRole database role - master (by default) / slave
     * @return DataSource
     * @throws Exception
     */
    public DataSource getOrCreateDataSource(String clusterName, String databaseRole) throws Exception {
        return getOrCreateDataSource(clusterName, null, databaseRole);
    }

    /**
     * Get or create DataSource with specified role for dal cluster on specified shard
     *
     * @param clusterName dal cluster name
     * @param shardIndex shard index
     * @param databaseRole database role - master (by default) / slave
     * @return DataSource
     * @throws Exception
     */
    public DataSource getOrCreateDataSource(String clusterName, Integer shardIndex, String databaseRole)
            throws Exception {
        return getOrCreateDataSource(clusterName, shardIndex,
                StringUtils.isEmpty(databaseRole) ? DatabaseRole.MASTER : DatabaseRole.parse(databaseRole));
    }

    private DataSource getOrCreateDataSource(String clusterName, Integer shardIndex, DatabaseRole databaseRole)
            throws Exception {
        TitanProvider provider = initTitanProvider(false);
        Cluster cluster = getCluster(provider, clusterName);
        ClusterInfo clusterInfo = buildClusterInfo(clusterName, cluster, shardIndex, databaseRole);
        provider.setup(new HashSet<>());
        return getOrCreateDataSource(provider, clusterInfo);
    }

    private Cluster getCluster(ClusterConfigProvider provider, String clusterName) {
        if (StringUtils.isEmpty(clusterName))
            throw new DalRuntimeException("clusterName should not be empty");
        return new ClusterManagerImpl(provider).getOrCreateCluster(clusterName);
    }

    private ClusterInfo buildClusterInfo(String clusterName, Cluster cluster, Integer shardIndex, DatabaseRole databaseRole) {
        int finalShard;
        if (shardIndex == null) {
            if (cluster.dbShardingEnabled())
                throw new IllegalArgumentException(String.format(
                        "shardIndex is necessary for sharding cluster '%s'", clusterName));
            finalShard = cluster.getAllDbShards().iterator().next();
        } else {
            if (!cluster.getAllDbShards().contains(shardIndex))
                throw new IllegalArgumentException(String.format(
                        "shard %d is not found for cluster '%s'", shardIndex, clusterName));
            finalShard = shardIndex;
        }
        DatabaseRole dbRole = databaseRole != null ? databaseRole : DatabaseRole.MASTER;
        if (dbRole == DatabaseRole.MASTER && cluster.getMasterOnShard(finalShard) == null)
            throw new IllegalStateException(String.format(
                    "master is not found for cluster '%s', shard %d", clusterName, finalShard));
        else if (dbRole == DatabaseRole.SLAVE) {
            List<Database> slaves = cluster.getSlavesOnShard(finalShard);
            if (slaves == null || slaves.size() == 0)
                throw new IllegalStateException(String.format(
                        "slave is not found for cluster '%s', shard %d", clusterName, finalShard));
            if (slaves.size() > 1)
                throw new UnsupportedOperationException(String.format(
                        "multi slaves are found for cluster '%s', shard %d, which is not supported yet",
                        clusterName, finalShard));
        }
        return new ClusterInfo(clusterName, finalShard, dbRole, cluster.dbShardingEnabled());
    }

    private DataSource getOrCreateDataSource(IntegratedConfigProvider provider, ClusterInfo clusterInfo) {
        return new DataSourceLocator(provider).getDataSource(clusterInfo);
    }

    /**
     * Get or create master DataSource for non-sharding dal cluster
     *
     * @param clusterName dal cluster name
     * @return DataSource
     * @throws Exception
     */
    public DataSource getOrCreateNonShardingDataSource(String clusterName) throws Exception {
        return getOrCreateDataSource(clusterName);
    }

    /**
     * Get or create master DataSources for dal cluster on all shards
     *
     * @param clusterName dal cluster name
     * @return DataSource
     * @throws Exception
     */
    public List<DataSource> getOrCreateAllMasterDataSources(String clusterName) throws Exception {
        return getOrCreateAllDataSourcesByRole(clusterName, DatabaseRole.MASTER);
    }

    private List<DataSource> getOrCreateAllDataSourcesByRole(String clusterName, DatabaseRole databaseRole)
            throws Exception {
        TitanProvider provider = initTitanProvider(false);
        Cluster cluster = getCluster(provider, clusterName);
        provider.setup(new HashSet<>());
        List<DataSource> dataSources = new LinkedList<>();
        for (Integer shard : cluster.getAllDbShards()) {
            ClusterInfo clusterInfo = buildClusterInfo(clusterName, cluster, shard, databaseRole);
            dataSources.add(getOrCreateDataSource(provider, clusterInfo));
        }
        return dataSources;
    }

    /**
     * This is only for cross environment usage
     * 
     * @param allInOneKey
     * @param svcUrl
     * @return
     * @throws Exception
     */
    public DataSource createTitanDataSource(String allInOneKey, String svcUrl) throws Exception {
        return new TitanDataSourceLocator().getDataSource(svcUrl, allInOneKey);
    }
}