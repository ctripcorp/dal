package com.ctrip.platform.dal.dao.configure;


import com.ctrip.framework.dal.cluster.client.config.DalConfigCustomizedOption;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;

public class DefaultDalConfigCustomizedOption implements DalConfigCustomizedOption {

    private String consistencyTypeCustomizedClass; // user customized drc-consistency-strategy class
    private boolean ignoreShardingResourceNotFound = false; // allow user not define sharding-customized-strategy class
    private boolean forceInitialize = false;
    private Integer shardIndex;
    private DatabaseRole databaseRole = DatabaseRole.MASTER;
    private String readStrategy;
    private String delegate;
    private String jdbcDriver;
    private String tag = "";
    private boolean queryConsistent;

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
        return forceInitialize;
    }

    @Override
    public Integer getShardIndex() {
        return shardIndex;
    }

    @Override
    public DatabaseRole getDatabaseRole() {
        return databaseRole;
    }

    @Override
    public String getRouteStrategy() {
        return readStrategy;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public String getDataSourceFactory() {
        return delegate;
    }

    @Override
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    @Override
    public boolean isQueryConsistent() {
        return queryConsistent;
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

    public DefaultDalConfigCustomizedOption readStrategy(String readStrategy) {
        this.readStrategy = readStrategy;
        return this;
    }

    public DefaultDalConfigCustomizedOption tag(String tag) {
        this.tag = tag;
        return this;
    }

    public DefaultDalConfigCustomizedOption delegate(String clazz) {
        this.delegate = clazz;
        return this;
    }

    public DefaultDalConfigCustomizedOption jdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
        return this;
    }

    public DefaultDalConfigCustomizedOption queryConsistent(boolean queryConsistent) {
        this.queryConsistent = queryConsistent;
        return this;
    }

    @Override
    public DefaultDalConfigCustomizedOption clone() {
        return new DefaultDalConfigCustomizedOption()
                .databaseRole(this.databaseRole)
                .shardIndex(this.shardIndex)
                .forceInitialize(this.forceInitialize)
                .ignoreShardingResourceNotFound(this.ignoreShardingResourceNotFound)
                .consistencyTypeCustomizedClass(this.consistencyTypeCustomizedClass)
                .readStrategy(this.readStrategy)
                .tag(this.tag)
                .delegate(this.delegate)
                .jdbcDriver(this.jdbcDriver)
                .queryConsistent(this.isQueryConsistent());
    }
}
