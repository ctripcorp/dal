package com.ctrip.platform.dal.dao.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfig;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.dao.configure.ClusterConfigProvider;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author c7ch23en
 */
public class ClusterManagerImpl implements ClusterManager {

    private final ClusterConfigProvider configProvider;
    private static final Map<String, Cluster> clusters = new ConcurrentHashMap<>();

    public ClusterManagerImpl(ClusterConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    @Override
    public Cluster getOrCreateCluster(String clusterName) {
        if (StringUtils.isEmpty(clusterName))
            throw new DalRuntimeException("cluster name is empty");
        clusterName = StringUtils.toTrimmedLowerCase(clusterName);
        Cluster cluster = clusters.get(clusterName);
        if (cluster == null)
            synchronized (clusters) {
                cluster = clusters.get(clusterName);
                if (cluster == null) {
                    cluster = createCluster(clusterName);
                    clusters.put(clusterName, cluster);
                }
            }
        return cluster;
    }

    private Cluster createCluster(String clusterName) {
        ClusterConfig config = configProvider.getClusterConfig(clusterName);
        return new DynamicCluster(config);
    }

}
