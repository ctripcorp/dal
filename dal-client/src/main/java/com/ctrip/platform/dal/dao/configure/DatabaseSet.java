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

public abstract class DatabaseSet implements IDatabaseSet {

    @Override
    public String getName() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public String getProvider() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public DatabaseCategory getDatabaseCategory() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public boolean isShardingSupported() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public boolean isTableShardingSupported(String tableName) {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public Map<String, DataBase> getDatabases() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public void validate(String shard) throws SQLException {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public Set<String> getAllShards() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public Set<String> getAllTableShards(String tableName) throws SQLException {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public DalShardingStrategy getStrategy() throws SQLException {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public List<DataBase> getMasterDbs() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public List<DataBase> getSlaveDbs() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public List<DataBase> getMasterDbs(String shard) {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public List<DataBase> getSlaveDbs(String shard) {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

    @Override
    public IIdGeneratorConfig getIdGenConfig() {
        throw new UnsupportedOperationException("This is an abstract DatabaseSet.");
    }

}
