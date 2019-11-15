package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.UnsupportedListenable;
import com.ctrip.framework.dal.cluster.client.cluster.DefaultCluster;
import com.ctrip.framework.dal.cluster.client.cluster.ShardStrategyProxy;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGenerator;
import com.ctrip.framework.dal.cluster.client.sharding.idgen.ClusterIdGeneratorConfig;
import com.ctrip.framework.dal.cluster.client.sharding.strategy.ShardStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class ClusterConfigImpl extends UnsupportedListenable<ClusterConfig> implements ClusterConfig {

    private String clusterName;
    private DatabaseCategory databaseCategory;
    private long version;
    private List<DatabaseShardConfig> databaseShardConfigs = new LinkedList<>();
    private ShardStrategy defaultShardStrategy;
    private List<ShardStrategy> shardStrategies = new LinkedList<>();
    private ClusterIdGeneratorConfig idGeneratorConfig;

    public ClusterConfigImpl(String clusterName, DatabaseCategory databaseCategory, long version) {
        this.clusterName = clusterName;
        this.databaseCategory = databaseCategory;
        this.version = version;
    }

    @Override
    public Cluster generate() {
        DefaultCluster cluster = new DefaultCluster(this);
        for (DatabaseShardConfig databaseShardConfig : databaseShardConfigs)
            cluster.addDatabaseShard(databaseShardConfig.generate());
        ShardStrategyProxy shardStrategy = new ShardStrategyProxy(defaultShardStrategy);
        for (ShardStrategy strategy : shardStrategies)
            shardStrategy.addStrategy(strategy);
        cluster.setShardStrategy(shardStrategy);
        cluster.setIdGeneratorConfig(idGeneratorConfig);
        return cluster;
    }

    @Override
    public boolean checkSwitchable(ClusterConfig newConfig) {
        if (newConfig instanceof ClusterConfigImpl) {
            ClusterConfigImpl ref = (ClusterConfigImpl) newConfig;
            return ref.getVersion() > version;
        }
        return false;
    }

    public String getClusterName() {
        return clusterName;
    }

    public DatabaseCategory getDatabaseCategory() {
        return databaseCategory;
    }

    public long getVersion() {
        return version;
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

    public void setIdGeneratorConfig(ClusterIdGeneratorConfig idGeneratorConfig) {
        this.idGeneratorConfig = idGeneratorConfig;
    }

}
