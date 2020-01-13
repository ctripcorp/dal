package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.Cluster;
import com.ctrip.framework.dal.cluster.client.base.Listener;
import com.ctrip.framework.dal.cluster.client.cluster.ClusterSwitchedEvent;
import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.client.DalConnectionLocator;
import com.ctrip.platform.dal.dao.strategy.ClusterShardStrategyAdapter;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.sharding.idgen.ClusterIdGeneratorConfigAdapter;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;

import java.sql.SQLException;
import java.util.*;

/**
 * @author c7ch23en
 */
public class ClusterDatabaseSet extends DatabaseSet {

    private String databaseSetName;
    private Cluster cluster;
    private ClusterShardStrategyAdapter shardStrategy;
    private DalConnectionLocator locator;
    private ClusterIdGeneratorConfigAdapter idGeneratorConfig;

    public ClusterDatabaseSet(String name, Cluster cluster, DalConnectionLocator locator) {
        this.databaseSetName = name;
        this.cluster = cluster;
        this.shardStrategy = new ClusterShardStrategyAdapter(cluster);
        this.locator = locator;
        this.idGeneratorConfig = new ClusterIdGeneratorConfigAdapter(cluster);
        registerListener();
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
        List<Database> clusterDatabases = cluster.getDatabases();
        Map<String, DataBase> dataBases = new HashMap<>();
        for (Database clusterDatabase : clusterDatabases) {
            DataBase dataBase = new ClusterDataBase(clusterDatabase);
            dataBases.put(dataBase.getName(), dataBase);
        }
        return dataBases;
    }

    @Override
    public void validate(String shard) throws SQLException {
        getShardIndex(shard);
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
        Database clusterDatabase = cluster.getMasterOnShard(0);
        List<DataBase> dataBases = new LinkedList<>();
        dataBases.add(new ClusterDataBase(clusterDatabase));
        return dataBases;
    }

    @Override
    public List<DataBase> getSlaveDbs() {
        List<Database> clusterDatabases = cluster.getSlavesOnShard(0);
        List<DataBase> dataBases = new LinkedList<>();
        for (Database clusterDatabase : clusterDatabases) {
            dataBases.add(new ClusterDataBase(clusterDatabase));
        }
        return dataBases;
    }

    @Override
    public List<DataBase> getMasterDbs(String shard) {
        Database clusterDatabase = cluster.getMasterOnShard(getShardIndex(shard));
        List<DataBase> dataBases = new LinkedList<>();
        dataBases.add(new ClusterDataBase(clusterDatabase));
        return dataBases;
    }

    @Override
    public List<DataBase> getSlaveDbs(String shard) {
        List<Database> clusterDatabases = cluster.getSlavesOnShard(getShardIndex(shard));
        List<DataBase> dataBases = new LinkedList<>();
        for (Database clusterDatabase : clusterDatabases) {
            dataBases.add(new ClusterDataBase(clusterDatabase));
        }
        return dataBases;
    }

    @Override
    public IIdGeneratorConfig getIdGenConfig() {
        return idGeneratorConfig;
    }

    public Cluster getCluster() {
        return cluster;
    }

    private int getShardIndex(String shard) {
        try {
            Integer shardIndex = StringUtils.toInt(shard);
            if (shardIndex == null || shardIndex < 0 || shardIndex >= cluster.getDbShardCount())
                throw new DalRuntimeException("shard is null or out of range");
            return shardIndex;
        } catch (Throwable t) {
            throw new DalRuntimeException(String.format("illegal shard: %s", shard));
        }
    }

    private void registerListener() {
        cluster.addListener(new Listener<ClusterSwitchedEvent>() {
            @Override
            public void onChanged(ClusterSwitchedEvent event) {
                locator.setupCluster(event.getCurrent());
                locator.uninstallCluster(event.getPrevious());
            }
        });
    }

}
