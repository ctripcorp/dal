package com.ctrip.framework.db.cluster.service;

import com.ctrip.framework.db.cluster.entity.Cluster;
import com.ctrip.framework.db.cluster.entity.Shard;
import com.ctrip.framework.db.cluster.entity.ShardInstance;
import com.ctrip.framework.db.cluster.enums.Deleted;
import com.ctrip.framework.db.cluster.enums.Enabled;
import com.ctrip.framework.db.cluster.service.repository.*;
import com.ctrip.framework.db.cluster.util.Constants;
import com.ctrip.framework.db.cluster.vo.dal.create.ClusterVo;
import com.ctrip.framework.db.cluster.vo.dal.create.ShardVo;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalCommand;
import com.ctrip.platform.dal.dao.DalHints;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by shenjie on 2019/8/15.
 */
@Slf4j
@Component
public class DalClusterManager {

    @Autowired
    private ClusterService clusterService;
    @Autowired
    private ShardService shardService;
    @Autowired
    private ShardInstanceService shardInstanceService;
    @Autowired
    private InstanceService instanceService;
    @Autowired
    private UserService userService;
    @Autowired
    private TitanKeyService titanKeyService;
    @Autowired
    private DalClusterProvider dalClusterProvider;

    public void addCluster(final ClusterVo clusterVo) throws SQLException {
        DalClient client = DalClientFactory.getClient(Constants.DATABASE_SET_NAME);
        client.execute(new DalCommand() {
            public boolean execute(DalClient client) throws SQLException {
                // createClusterSets cluster
                int clusterId = clusterService.addAndGetId(clusterVo);

                // createClusterSets shards
                addShards(clusterVo.deprGetShards(), clusterId);
                return true;
            }
        }, new DalHints());
    }

    public void addShard(String clusterName, final List<ShardVo> shards) throws SQLException {
        Cluster clusterInDB = clusterService.findCluster(
                clusterName, Deleted.un_deleted, null
        );
        Preconditions.checkNotNull(clusterInDB, "cluster[" + clusterName + "]不存在");
        Integer clusterId = clusterInDB.getId();

        for (ShardVo shard : shards) {
            Integer shardIndex = shard.deprGetIndex();
            Shard shardInDB = shardService.findShardsByClusterIdAndShardIndex(clusterId, shardIndex);
            Preconditions.checkArgument(shardInDB == null, "shardIndex[" + shardIndex + "]已存在");
        }

        DalClient client = DalClientFactory.getClient(Constants.DATABASE_SET_NAME);
        client.execute(new DalCommand() {
            public boolean execute(DalClient client) throws SQLException {
                // createClusterSets shards
                addShards(shards, clusterId);
                return true;
            }
        }, new DalHints());
    }

    public ClusterVo getCluster(String clusterName) throws SQLException {
        return dalClusterProvider.getCluster(clusterName);
    }

    public void updateShards(List<ShardVo> shardVos) throws SQLException {
        for (ShardVo shard : shardVos) {
            String dbName = shard.getDbName();
            // todo: dbName是否唯一
            List<Shard> shardsInDB = shardService.findShardsByDbName(dbName);
            if (shardsInDB != null && !shardsInDB.isEmpty()) {
                Shard shardInDB = shardsInDB.get(0);
                String masterDomain = shard.getMaster().getDomain();
                String masterDomainInDB = shardInDB.getMasterDomain();
                Preconditions.checkArgument(masterDomain.equalsIgnoreCase(masterDomainInDB),
                        "master domain not equals domain in db");

                updateShard(shard, shardInDB.getId());
            }
        }
    }

    private void updateShard(ShardVo shard, Integer shardId) throws SQLException {
        // disable old instance
        List<ShardInstance> shardInstances = shardInstanceService.findByShardId(shardId);
        for (ShardInstance shardInstance : shardInstances) {
            shardInstanceService.update(shardInstance);
        }

        // createClusterSets instances
        instanceService.addInstances(shard, shardId);
    }

    private void addShards(List<ShardVo> shards, Integer clusterId) throws SQLException {
        for (ShardVo shard : shards) {
            addShard(shard, clusterId);
        }
    }

    private void addShard(ShardVo shard, Integer clusterId) throws SQLException {
        int shardId = shardService.addAndGetId(shard, clusterId);

        // createClusterSets instances
        instanceService.addInstances(shard, shardId);

        // createClusterSets user
        Map<String, Integer> userIds = userService.addAndGetIds(shard.deprGetUsers(), shardId);

        // createClusterSets titan key
        titanKeyService.add(shard.deprGetTitanKeys(), userIds);
    }

}
