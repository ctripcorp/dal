package com.ctrip.platform.dal.cluster;

import com.ctrip.platform.dal.cluster.base.Listenable;
import com.ctrip.platform.dal.cluster.cluster.ClusterSwitchedEvent;
import com.ctrip.platform.dal.cluster.cluster.ClusterType;
import com.ctrip.platform.dal.cluster.config.DalConfigCustomizedOption;
import com.ctrip.platform.dal.cluster.config.LocalizationConfig;
import com.ctrip.platform.dal.cluster.database.Database;
import com.ctrip.platform.dal.cluster.database.DatabaseCategory;
import com.ctrip.platform.dal.cluster.multihost.ClusterRouteStrategyConfig;
import com.ctrip.platform.dal.cluster.shard.DatabaseShard;
import com.ctrip.platform.dal.cluster.sharding.context.DbShardContext;
import com.ctrip.platform.dal.cluster.sharding.context.TableShardContext;
import com.ctrip.platform.dal.cluster.sharding.idgen.ClusterIdGeneratorConfig;

import java.sql.Wrapper;
import java.util.List;
import java.util.Set;

/**
 * @author c7ch23en
 */
public interface Cluster extends Listenable<ClusterSwitchedEvent>, Wrapper {

    String getClusterName();

    ClusterType getClusterType();

    DatabaseCategory getDatabaseCategory();

    boolean dbShardingEnabled();

    Integer getDbShard(String tableName, DbShardContext context);

    Set<Integer> getAllDbShards();

    boolean tableShardingEnabled(String tableName);

    String getTableShard(String tableName, TableShardContext context);

    Set<String> getAllTableShards(String tableName);

    String getTableShardSeparator(String tableName);

    List<Database> getDatabases();

    Database getMasterOnShard(int shardIndex);

    List<Database> getSlavesOnShard(int shardIndex);

    ClusterIdGeneratorConfig getIdGeneratorConfig();

    ClusterRouteStrategyConfig getRouteStrategyConfig();

    LocalizationConfig getLocalizationConfig();

    LocalizationConfig getLastLocalizationConfig();

    DalConfigCustomizedOption getCustomizedOption();

    DatabaseShard getDatabaseShard(int shardIndex);

}
