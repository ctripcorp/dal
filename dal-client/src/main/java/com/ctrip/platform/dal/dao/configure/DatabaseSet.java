package com.ctrip.platform.dal.dao.configure;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;

public interface DatabaseSet {

    String getName();

    String getProvider();

    DatabaseCategory getDatabaseCategory();

    boolean isShardingSupported();

    boolean isTableShardingSupported(String tableName);

    Map<String, DataBase> getDatabases();

    void validate(String shard) throws SQLException;

    Set<String> getAllShards();

    Set<String> getAllTableShards(String tableName) throws SQLException;

    DalShardingStrategy getStrategy() throws SQLException;

    List<DataBase> getMasterDbs();

    List<DataBase> getSlaveDbs();

    List<DataBase> getMasterDbs(String shard);

    List<DataBase> getSlaveDbs(String shard);

    IIdGeneratorConfig getIdGenConfig();

}
