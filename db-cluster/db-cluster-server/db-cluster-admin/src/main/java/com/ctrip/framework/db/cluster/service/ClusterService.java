package com.ctrip.framework.db.cluster.service;

import com.alibaba.fastjson.JSON;
import com.ctrip.framework.db.cluster.entity.*;
import com.ctrip.framework.db.cluster.utils.EnvUtil;
import com.ctrip.framework.db.cluster.utils.HttpUtil;
import com.dianping.cat.Cat;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by taochen on 2019/11/6.
 */
@Service
public class ClusterService {
    private static final String DB_CLUSTER_GET_CLUSTERS_FAT = "http://service.dbcluster.fat-1.qa.nt.ctripcorp.com/api/dal/v1/clusters/?operator=%s&effective=%s";

    private static final String DB_CLUSTER_GET_CLUSTERS_UAT = "http://service.dbcluster.uat.qa.nt.ctripcorp.com/api/dal/v1/clusters/?operator=%s&effective=%s";

    private static final String DB_CLUSTER_GET_CLUSTERS_PRO = "http://service.dbcluster.ctripcorp.com/api/dal/v1/clusters/?operator=%s&effective=%s";

    private static final String DB_CLUSTER_GET_CLUSTER_INFO_FAT = "http://service.dbcluster.fat-1.qa.nt.ctripcorp.com/api/dal/v1/clusters/%s?operator=%s&effective=%s";

    private static final String DB_CLUSTER_GET_CLUSTER_INFO_UAT = "http://service.dbcluster.uat.qa.nt.ctripcorp.com/api/dal/v1/clusters/%s?operator=%s&effective=%s";

    private static final String DB_CLUSTER_GET_CLUSTER_INFO_PRO = "http://service.dbcluster.ctripcorp.com/api/dal/v1/clusters/%s?operator=%s&effective=%s";

    private static final String ADMIN_USER = "chentao";

    private ConcurrentHashMap<String, Cluster> clusterCache = new ConcurrentHashMap<>();

    public ClusterResponse getCluster(String clusterName) {
        String env = EnvUtil.getEnv();
        String url = "FAT".equalsIgnoreCase(env) ? DB_CLUSTER_GET_CLUSTER_INFO_FAT : "UAT".equalsIgnoreCase(env) ?
                DB_CLUSTER_GET_CLUSTER_INFO_UAT : DB_CLUSTER_GET_CLUSTER_INFO_PRO;
        String formatUrl = String.format(url, clusterName, ADMIN_USER, "true");
        ClusterResponse clusterResponse = null;
        try {
            Map<String, String> parameters = new HashMap<>();
            clusterResponse = HttpUtil.getJSONEntity(ClusterResponse.class, formatUrl, parameters, HttpMethod.HttpGet);
        } catch (Exception e) {
            Cat.logError("get cluster[" + clusterName +"] fail!", e);
        }
        clusterCache.put(clusterResponse.getResult().getClusterName(), clusterResponse.getResult());
        return clusterResponse;
    }

    public ClusterListResponse getAllClusters() {
        //String clusters = "{ \"status\": 200, \"message\": \"Query cluster success\", \"result\": [\"cluster1\", \"cluster2\", \"cluster3\"]}";
        String env = EnvUtil.getEnv();
        String url = "FAT".equalsIgnoreCase(env) ? DB_CLUSTER_GET_CLUSTERS_FAT : "UAT".equalsIgnoreCase(env) ?
                DB_CLUSTER_GET_CLUSTERS_UAT : DB_CLUSTER_GET_CLUSTERS_PRO;
        String formatUrl = String.format(url, ADMIN_USER, "true");
        ClusterListResponse clusterListResponse = null;
        try {
            Map<String, String> parameters = new HashMap<>();
            clusterListResponse = HttpUtil.getJSONEntity(ClusterListResponse.class, formatUrl, parameters, HttpMethod.HttpGet);
        } catch (Exception e) {
            Cat.logError("get all cluster fail!", e);
        }
        //ClusterListResponse clusterListResponse = JSON.parseObject(clusters, ClusterListResponse.class);
        return clusterListResponse;
    }

    public List<Zone> findClusterZones(String clusterName) {
        return clusterCache.get(clusterName).getZones();
    }

    public List<Shard> getShards(String clusterName, String zoneId) {
        List<Shard> shardList = new ArrayList<>();
        for (Zone zone : clusterCache.get(clusterName).getZones()) {
            if (zone.getZoneId().equals(zoneId)) {
                shardList = zone.getShards();
            }
        }
        return shardList;
    }

    public List<Instance> getInstancesByZoneIdAndShardIndex(String clusterName, String zoneId, int shardIndex, String role) {
        List<Instance> reslut = new ArrayList<>();
        List<Zone> zoneList = clusterCache.get(clusterName).getZones();
        List<Shard> shardList = null;
        for (Zone zone : zoneList) {
            if (zone.getZoneId().equals(zoneId)) {
                shardList = zone.getShards();
                break;
            }
        }
        if (shardList != null) {
            Shard shard = shardList.get(shardIndex);
            if (ClusterConstant.SHARD_ROLE_MASTER.equalsIgnoreCase(role)) {
                reslut = Arrays.asList(shard.getMaster().getInstance());
            }
            else if (ClusterConstant.SHARD_ROLE_SLAVE.equalsIgnoreCase(role)) {
                reslut = shard.getSlave().getInstances();
            }
            else {
                reslut = shard.getRead().getInstances();
            }
        }
        return reslut;
    }
}
