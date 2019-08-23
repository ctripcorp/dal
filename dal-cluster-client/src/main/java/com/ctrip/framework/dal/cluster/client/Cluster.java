package com.ctrip.framework.dal.cluster.client;

import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.sharding.context.DbShardContext;
import com.ctrip.framework.dal.cluster.client.sharding.context.TableShardContext;

import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface Cluster {

    String getClusterName();

    DatabaseCategory getDatabaseCategory();

    int getDbShardCount();

    Integer getDbShard(String tableName, DbShardContext context);

    boolean tableShardingEnabled(String tableName);

    String getTableShard(String tableName, TableShardContext context);

    Set<String> getAllTableShards(String tableName);

    String getTableShardSeparator(String tableName);

    List<Database> getDatabases();

    Database getMasterOnShard(int shardIndex);

    List<Database> getSlavesOnShard(int shardIndex);

}
