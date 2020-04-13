package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.cluster.DynamicCluster;
import com.ctrip.platform.dal.dao.helper.DalElementFactory;
import com.ctrip.platform.dal.dao.log.ILogger;

import java.util.List;
import java.util.Map;

/**
 * @author c7ch23en
 */
public class ClusterDatabaseSetAdapter implements DatabaseSetAdapter {

    private static final ILogger LOGGER = DalElementFactory.DEFAULT.getILogger();

    private ClusterInfoProvider clusterInfoProvider;
    private ClusterConfigProvider clusterConfigProvider;
    private DalConnectionLocator connectionLocator;

    public ClusterDatabaseSetAdapter(DalConnectionLocator connectionLocator) {
        this.connectionLocator = connectionLocator;
        this.clusterInfoProvider = connectionLocator.getIntegratedConfigProvider();
        this.clusterConfigProvider = connectionLocator.getIntegratedConfigProvider();
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
         * 1. mysql
         * 2. no shard strategy
         * 4. no idgen config
         * 5. one master, no slaves
         */
        boolean adaptable = true;
        if (defaultDatabaseSet.getDatabaseCategory() != DatabaseCategory.MySql)
            adaptable = false;
        if (defaultDatabaseSet.getShardingStrategy() != null)
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
            List<DataBase> masters = defaultDatabaseSet.getMasterDbs();
            if (masters != null && masters.size() == 1) {
                String databaseKey = masters.iterator().next().getConnectionString();
                Map<String, DalConnectionString> failedConnectionStrings = DataSourceConfigureLocatorManager.
                        getInstance().getFailedConnectionStrings();
                if (failedConnectionStrings == null || !failedConnectionStrings.containsKey(databaseKey)) {
                    ClusterInfo clusterInfo = clusterInfoProvider.getClusterInfo(databaseKey);
                    if (clusterInfo != null && clusterInfo.getRole() == DatabaseRole.MASTER &&
                            !clusterInfo.dbSharding()) {
                        String clusterName = clusterInfo.getClusterName();
                        ClusterConfig clusterConfig = clusterConfigProvider.getClusterConfig(clusterName);
                        Cluster cluster = new DynamicCluster(clusterConfig);
                        return new ClusterDatabaseSet(defaultDatabaseSet.getName(), cluster, connectionLocator);
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Adapt DefaultDatabaseSet to ClusterDatabaseSet exception", t);
        }
        return null;
    }

}
