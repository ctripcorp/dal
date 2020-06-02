package com.ctrip.datasource.helper.cluster;

import com.ctrip.datasource.titan.TitanProvider;
import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterType;
import com.ctrip.framework.dal.cluster.client.cluster.DrcCluster;
import com.ctrip.platform.dal.dao.cluster.ClusterManager;
import com.ctrip.platform.dal.dao.cluster.ClusterManagerImpl;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Optional;

/**
 * @author c7ch23en
 */
public class DalClusterHelper {

    private final ClusterManager clusterManager;

    public static Optional<Integer> tryGetUcsStrategyId(String clusterName) {
        Cluster cluster = getInstance().clusterManager.getOrCreateCluster(clusterName);
        if (cluster.getClusterType() == ClusterType.DRC) {
            try {
                DrcCluster drcCluster = cluster.unwrap(DrcCluster.class);
                return Optional.of(drcCluster.getLocalizationConfig().getUnitStrategyId());
            } catch (SQLException e) {
                throw new DalRuntimeException("Cluster unwrap failed", e);
            }
        }
        return Optional.empty();
    }

    private static volatile DalClusterHelper instance;

    private static DalClusterHelper getInstance() {
        if (instance == null) {
            synchronized (DalClusterHelper.class) {
                if (instance == null) {
                    try {
                        instance = new DalClusterHelper();
                    } catch (Exception e) {
                        throw new DalRuntimeException("DalClusterHelper initialization failed", e);
                    }
                }
            }
        }
        return instance;
    }

    private DalClusterHelper() throws Exception {
        TitanProvider provider = new TitanProvider();
        provider.initialize(new HashMap<>());
        clusterManager = new ClusterManagerImpl(provider);
    }

}
