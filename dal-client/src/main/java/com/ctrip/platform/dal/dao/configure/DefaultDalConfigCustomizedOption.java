package com.ctrip.platform.dal.dao.configure;

import com.ctrip.framework.dal.cluster.client.config.DalConfigCustomizedOption;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;

public class DefaultDalConfigCustomizedOption implements DalConfigCustomizedOption {

    private String consistencyTypeCustomizedClass; // user customized drc-consistency-strategy class
    private boolean ignoreShardingResourceNotFound = false; // allow user not define sharding-customized-strategy class
    private boolean forceInitialize = false;
    private Integer shardIndex;
    private DatabaseRole databaseRole = DatabaseRole.MASTER;

    @Override
    public String getConsistencyTypeCustomizedClass() {
        return consistencyTypeCustomizedClass;
    }

    @Override
    public boolean isIgnoreShardingResourceNotFound() {
        return ignoreShardingResourceNotFound;
    }

    @Override
    public boolean isForceInitialize() {
        return false;
    }

    @Override
    public Integer getShardIndex() {
        return null;
    }

    @Override
    public DatabaseRole getDatabaseRole() {
        return null;
    }

    public DefaultDalConfigCustomizedOption consistencyTypeCustomizedClass(String clazz) {
        this.consistencyTypeCustomizedClass = clazz;
        return this;
    }

    public DefaultDalConfigCustomizedOption ignoreShardingResourceNotFound(boolean allowDefault) {
        this.ignoreShardingResourceNotFound = allowDefault;
        return this;
    }

    public DefaultDalConfigCustomizedOption forceInitialize(boolean forceInitialize) {
        this.forceInitialize = forceInitialize;
        return this;
    }

    public DefaultDalConfigCustomizedOption shardIndex(Integer shardIndex) {
        this.shardIndex = shardIndex;
        return this;
    }

    public DefaultDalConfigCustomizedOption databaseRole(DatabaseRole databaseRole) {
        this.databaseRole = databaseRole;
        return this;
    }

    @Override
    public DefaultDalConfigCustomizedOption clone() {
        return new DefaultDalConfigCustomizedOption()
                .databaseRole(this.databaseRole)
                .shardIndex(this.shardIndex)
                .forceInitialize(this.forceInitialize)
                .ignoreShardingResourceNotFound(this.ignoreShardingResourceNotFound)
                .consistencyTypeCustomizedClass(this.consistencyTypeCustomizedClass);
    }
}
