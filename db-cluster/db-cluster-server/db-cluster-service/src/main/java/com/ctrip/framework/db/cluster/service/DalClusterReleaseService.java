package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.domain.plugin.dal.release.ReleaseCluster;
import com.ctrip.framework.db.cluster.domain.PluginResponse;
import com.ctrip.framework.db.cluster.domain.PluginStatusCode;
import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.Shard;
import com.ctrip.framework.db.cluster.exception.DBClusterServiceException;
import com.ctrip.framework.db.cluster.service.builder.DalClusterConfigBuilder;
import com.ctrip.framework.db.cluster.service.checker.DalClusterReleaseChecker;
import com.ctrip.framework.db.cluster.service.plugin.DalPluginService;
import com.ctrip.framework.db.cluster.service.repository.ClusterService;
import com.ctrip.framework.db.cluster.service.repository.ShardService;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Created by shenjie on 2019/6/28.
 */
@Slf4j
@Service
public class DalClusterReleaseService {

    @Autowired
    private DalPluginService dalPluginService;
    @Autowired
    private DalClusterConfigBuilder dalClusterConfigBuilder;
    @Autowired
    private DalClusterReleaseChecker dalClusterReleaseChecker;
    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ShardService shardService;
    @Autowired
    private DalClusterManager dalClusterManager;

    public void addClusters(String clusterName, String env, String operator) throws Exception {
        List<ReleaseCluster> clusters = buildClusters(clusterName, env);

        PluginResponse pluginResponse = dalPluginService.releaseClusters(clusters, env, operator);
        if (pluginResponse != null && PluginStatusCode.OK != pluginResponse.getStatus()) {
            throw new DBClusterServiceException(pluginResponse.getMessage());
        }

        clusterService.updateReleaseInfo(Lists.newArrayList(clusterName));
    }

    public void updateClusters(List<ShardVo> shardVos, String env, String operator) throws SQLException {
        // find clusterNames
        List<String> dbNames = getDBNames(shardVos);
        List<Shard> shards = shardService.findShardsByDBNames(dbNames);
        Set<Integer> clusterIds = getClusterIds(shards);
        List<Cluster> clusters = clusterService.findByIds(Lists.newArrayList(clusterIds));
        Set<String> clusterNames = getClusterNames(clusters);

        // update clusters config to plugin
        updateClusters(clusterNames, env, operator);

        // update clusters in db
        clusterService.updateReleaseInfo(Lists.newArrayList(clusterNames));
    }

    private void updateClusters(Set<String> clusterNames, String env, String operator) throws SQLException {
        // get clusters config
        List<ReleaseCluster> allClusters = Lists.newArrayList();
        for (String clusterName : clusterNames) {
            List<ReleaseCluster> clusters = buildClusters(clusterName, env);
            allClusters.addAll(clusters);
        }

        // update clusters config to plugin
        PluginResponse pluginResponse = dalPluginService.updateClusters(allClusters, env, operator);
        if (pluginResponse != null && PluginStatusCode.OK != pluginResponse.getStatus()) {
            throw new DBClusterServiceException(pluginResponse.getMessage());
        }
    }

    private List<ReleaseCluster> buildClusters(String clusterName, String env) throws SQLException {
        ClusterVo cluster = dalClusterManager.getCluster(clusterName);
        dalClusterReleaseChecker.check(cluster, env);

        // build clusters config (all userTags)
        List<ReleaseCluster> clusters = dalClusterConfigBuilder.build(cluster);

        return clusters;
    }

    private Set<String> getClusterNames(List<Cluster> clusters) {
        Set<String> clusterNames = Sets.newHashSet();
        for (Cluster cluster : clusters) {
            clusterNames.add(cluster.getClusterName());
        }
        return clusterNames;
    }

    private List<String> getDBNames(List<ShardVo> shards) {
        List<String> dbNames = Lists.newArrayList();
        for (ShardVo shard : shards) {
            dbNames.add(shard.getDbName());
        }
        return dbNames;
    }

    private Set<Integer> getClusterIds(List<Shard> shards) {
        Set<Integer> clusterIds = Sets.newHashSet();
        for (Shard shard : shards) {
            clusterIds.add(shard.getClusterId());
        }
        return clusterIds;
    }

}
