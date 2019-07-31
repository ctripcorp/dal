package com.ctrip.framework.dal.cluster.client.cluster;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.config.ClusterConfigImpl;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.shard.DatabaseShard;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class DefaultCluster implements Cluster {

    private ClusterConfigImpl clusterConfig;
    private Map<Integer, DatabaseShard> databaseShards = new HashMap<>();
    private ShardStrategyProxy shardStrategyProxy;

    public DefaultCluster(ClusterConfigImpl clusterConfig) {
        this.clusterConfig = clusterConfig;
    }

    @Override
    public String getClusterName() {
        return clusterConfig.getClusterName();
    }

    @Override
    public DatabaseCategory getDatabaseCategory() {
        return clusterConfig.getDatabaseCategory();
    }

    @Override
    public int getDbShardCount() {
        return databaseShards.size();
    }

    @Override
    public Integer getDbShard(String tableName, DbShardContext context) {
        return shardStrategyProxy.getDbShard(tableName, context);
    }

    @Override
    public boolean tableShardingEnabled(String tableName) {
        return shardStrategyProxy.tableShardingEnabled(tableName);
    }

    @Override
    public String getTableShard(String tableName, TableShardContext context) {
        return shardStrategyProxy.getTableShard(tableName, context);
    }

    @Override
    public Set<String> getAllTableShards(String tableName) {
        return shardStrategyProxy.getAllTableShards(tableName);
    }

    @Override
    public String getTableShardSeparator(String tableName) {
        return shardStrategyProxy.getTableShardSeparator(tableName);
    }

    public void addDatabaseShard(DatabaseShard databaseShard) {
        databaseShards.put(databaseShard.getShardIndex(), databaseShard);
    }

    public void setShardStrategy(ShardStrategyProxy shardStrategyProxy) {
        this.shardStrategyProxy = shardStrategyProxy;
    }

}
