package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.cluster.ClusterManager;
import com.ctrip.platform.dal.dao.cluster.ClusterManagerImpl;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.DalLogTypes;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterDatabaseSetAdapter implements DatabaseSetAdapter {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private final ClusterInfoProvider clusterInfoProvider;
    private final ClusterManager clusterManager;
    private final DalConnectionLocator connectionLocator;

    public ClusterDatabaseSetAdapter(DalConnectionLocator connectionLocator) {
        this.connectionLocator = connectionLocator;
        this.clusterInfoProvider = connectionLocator.getIntegratedConfigProvider();
        this.clusterManager = new ClusterManagerImpl(connectionLocator.getIntegratedConfigProvider());
    }

    @Override
    public DatabaseSet adapt(DatabaseSet original) {
        if (original instanceof DefaultDatabaseSet) {
            DefaultDatabaseSet defaultDatabaseSet = (DefaultDatabaseSet) original;
            if (adaptable(defaultDatabaseSet)) {
                ClusterDatabaseSet clusterDatabaseSet = tryAdapt(defaultDatabaseSet);
                return clusterDatabaseSet != null ? clusterDatabaseSet : original;
            }
        }
        return original;
    }

    private boolean adaptable(DefaultDatabaseSet defaultDatabaseSet) {
        /*
         * 0. no subEnv, not aws
         * 1. mysql
         * 2. no shard strategy
         * 4. no idgen config
         * 5. one master, no slaves
         */
        boolean adaptable = true;
        if (defaultDatabaseSet.getDatabaseCategory() != DatabaseCategory.MySql)
            adaptable = false;
        if (defaultDatabaseSet.getStrategyNullable() != null)
            adaptable = false;
        if (defaultDatabaseSet.getIdGenConfig() != null)
            adaptable = false;
        List<DataBase> masters = defaultDatabaseSet.getMasterDbs();
        List<DataBase> slaves = defaultDatabaseSet.getSlaveDbs();
        if (masters == null || masters.size() != 1)
            adaptable = false;
        if (slaves != null && slaves.size() > 0)
            adaptable = false;
        return adaptable;
    }

    private ClusterDatabaseSet tryAdapt(DefaultDatabaseSet defaultDatabaseSet) {
        try {
            DataBase master = defaultDatabaseSet.getMasterDbs().iterator().next();
            if ((master instanceof DefaultDataBase) && !(master instanceof ProviderDataBase)) {
                String databaseKey = master.getConnectionString();
                Map<String, DalConnectionString> failedConnectionStrings = DataSourceConfigureLocatorManager.
                        getInstance().getFailedConnectionStrings();
                if (failedConnectionStrings == null || !failedConnectionStrings.containsKey(databaseKey)) {
                    ClusterInfo clusterInfo = clusterInfoProvider.getClusterInfo(databaseKey);
                    if (clusterInfo != null && clusterInfo.getRole() == DatabaseRole.MASTER &&
                            !clusterInfo.dbSharding()) {
                        String clusterName = clusterInfo.getClusterName();
                        //todo-lhj 弄明白怎么回事
                        Cluster cluster = clusterManager.getOrCreateCluster(clusterName, new DefaultDalConfigCustomizedOption());
                        if (checkCluster(cluster)) {
                            LOGGER.logEvent(DalLogTypes.DAL_VALIDATION, "ClusterAdaptSucceeded",
                                    String.format("databaseSet: %s, clusterName: %s",
                                            defaultDatabaseSet.getName(), clusterName));
                            return new ClusterDatabaseSet(defaultDatabaseSet.getName(), cluster, connectionLocator);
                        }
                    }
                }
            }
            LOGGER.logEvent(DalLogTypes.DAL_VALIDATION, "ClusterAdaptSkipped", String.format("databaseSet: %s",
                    defaultDatabaseSet.getName()));
        } catch (Throwable t) {
            LOGGER.logEvent(DalLogTypes.DAL_VALIDATION, "ClusterAdaptFailed", String.format("databaseSet: %s",
                    defaultDatabaseSet.getName()));
            LOGGER.warn("Adapt DefaultDatabaseSet to ClusterDatabaseSet exception", t);
        }
        return null;
    }

    private boolean checkCluster(Cluster cluster) {
        if (!cluster.dbShardingEnabled()) {
            int theOnlyShard = cluster.getAllDbShards().iterator().next();
            Database master = cluster.getMasterOnShard(theOnlyShard);
            List<Database> slaves = cluster.getSlavesOnShard(theOnlyShard);
            return master != null && (slaves == null || slaves.isEmpty());
        }
        return false;
    }

}
