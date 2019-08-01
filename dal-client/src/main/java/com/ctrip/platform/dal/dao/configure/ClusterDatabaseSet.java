package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.strategy.ClusterShardStrategyAdapter;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class ClusterDatabaseSet implements DatabaseSet {

    private String databaseSetName;
    private Cluster cluster;
    private ClusterShardStrategyAdapter shardStrategy;

    public ClusterDatabaseSet(String name, Cluster cluster) {
        this.databaseSetName = name;
        this.cluster = cluster;
        this.shardStrategy = new ClusterShardStrategyAdapter(cluster);
    }

    @Override
    public String getName() {
        return databaseSetName;
    }

    @Override
    public String getProvider() {
        return cluster.getDatabaseCategory().getValue();
    }

    @Override
    public DatabaseCategory getDatabaseCategory() {
        return DatabaseCategory.matchWith(cluster.getDatabaseCategory());
    }

    @Override
    public boolean isShardingSupported() {
        return shardStrategy.isShardingByDb();
    }

    @Override
    public boolean isTableShardingSupported(String tableName) {
        return shardStrategy.isShardingEnable(tableName);
    }

    @Override
    public Map<String, DataBase> getDatabases() {
        return null;
    }

    @Override
    public void validate(String shard) throws SQLException {
        try {
            Integer shardIndex = StringUtils.toInt(shard);
            if (shardIndex == null || shardIndex < 0 || shardIndex >= cluster.getDbShardCount())
                throw new DalRuntimeException("shard is null or out of range");
        } catch (Throwable t) {
            throw new SQLException(String.format("illegal shard: %s", shard));
        }
    }

    @Override
    public Set<String> getAllShards() {
        Set<String> allShards = new HashSet<>();
        for (int i = 0; i < cluster.getDbShardCount(); i++)
            allShards.add(String.valueOf(i));
        return allShards;
    }

    @Override
    public Set<String> getAllTableShards(String tableName) {
        return cluster.getAllTableShards(tableName);
    }

    @Override
    public DalShardingStrategy getStrategy() {
        return shardStrategy;
    }

    @Override
    public List<DataBase> getMasterDbs() {
        return null;
    }

    @Override
    public List<DataBase> getSlaveDbs() {
        return null;
    }

    @Override
    public List<DataBase> getMasterDbs(String shard) {
        return null;
    }

    @Override
    public List<DataBase> getSlaveDbs(String shard) {
        return null;
    }

    @Override
    public IIdGeneratorConfig getIdGenConfig() {
        return null;
    }

}
