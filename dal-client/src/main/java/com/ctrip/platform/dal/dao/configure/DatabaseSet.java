package com.ctrip.platform.dal.dao.configure;

import java.sql.SQLException;
import java.util.*;

import com.ctrip.framework.dal.cluster.client.util.StringUtils;
import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.sharding.idgen.IIdGeneratorConfig;

public abstract class DatabaseSet implements IDatabaseSet {

    private final Map<String, String> properties;

    public DatabaseSet() {
        properties = null;
    }

    public DatabaseSet(Map<String, String> properties) {
        this.properties = properties;
    }

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

    public String getSetting(String key) {
        return properties != null ? properties.get(key) : null;
    }

    public Integer getSettingAsInt(String key) {
        String value = getSetting(key);
        return !StringUtils.isEmpty(value) ? Integer.parseInt(value) : null;
    }

    public Long getSettingAsLong(String key) {
        String value = getSetting(key);
        return !StringUtils.isEmpty(value) ? Long.parseLong(value) : null;
    }

    public Boolean getSettingAsBool(String key) {
        String value = getSetting(key);
        return !StringUtils.isEmpty(value) ? Boolean.parseBoolean(value) : null;
    }

}
