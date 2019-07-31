package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.IgnoredListenable;
import com.ctrip.framework.dal.cluster.client.cluster.DefaultCluster;
import com.ctrip.framework.dal.cluster.client.cluster.ShardStrategyProxy;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.sharding.strategy.ShardStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class ClusterConfigImpl extends IgnoredListenable<ClusterConfig> implements ClusterConfig {

    private String clusterName;
    private DatabaseCategory databaseCategory;
    private long version;
    private List<DatabaseShardConfig> databaseShardConfigs = new LinkedList<>();
    private ShardStrategy defaultShardStrategy;
    private List<ShardStrategy> shardStrategies = new LinkedList<>();

    public ClusterConfigImpl(String clusterName, DatabaseCategory databaseCategory, long version) {
        this.clusterName = clusterName;
        this.databaseCategory = databaseCategory;
        this.version = version;
    }

    @Override
    public Cluster generateCluster() {
        DefaultCluster cluster = new DefaultCluster(this);
        for (DatabaseShardConfig databaseShardConfig : databaseShardConfigs)
            cluster.addDatabaseShard(databaseShardConfig.generateDatabaseShard());
        ShardStrategyProxy shardStrategy = new ShardStrategyProxy(defaultShardStrategy);
        for (ShardStrategy strategy : shardStrategies)
            shardStrategy.addStrategy(strategy);
        cluster.setShardStrategy(shardStrategy);
        return cluster;
    }

    public String getClusterName() {
        return clusterName;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public void addDatabaseShardConfig(DatabaseShardConfig databaseShardConfig) {
        databaseShardConfigs.add(databaseShardConfig);
    }

    public void setDefaultStrategy(ShardStrategy shardStrategy) {
        if (shardStrategy == null)
            return;
        if (defaultShardStrategy != null)
            throw new ClusterConfigException("default shard strategy already defined");
        defaultShardStrategy = shardStrategy;
        addShardStrategy(shardStrategy);
    }

    public void addShardStrategy(ShardStrategy shardStrategy) {
        if (shardStrategy != null)
            shardStrategies.add(shardStrategy);
    }

}
