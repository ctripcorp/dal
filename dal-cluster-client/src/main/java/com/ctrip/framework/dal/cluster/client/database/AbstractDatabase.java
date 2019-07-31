package com.ctrip.framework.dal.cluster.client.database;

import com.ctrip.framework.dal.cluster.client.config.DatabaseConfigImpl;
import com.ctrip.framework.dal.cluster.client.config.DatabaseShardConfigImpl;

/**
 * @author c7ch23en
 */
public abstract class AbstractDatabase implements Database, ConnectionString {

    private volatile DatabaseConfigImpl databaseConfig;
    private String primaryConnectionUrl;
    private String failOverConnectionUrl;

    protected AbstractDatabase(DatabaseConfigImpl databaseConfig) {
        this.databaseConfig = databaseConfig;
        this.primaryConnectionUrl = buildPrimaryConnectionUrl();
        this.failOverConnectionUrl = buildFailOverConnectionUrl();
    }

    protected abstract String buildPrimaryConnectionUrl();

    protected abstract String buildFailOverConnectionUrl();

    @Override
    public boolean isMaster() {
        return getDatabaseConfig().getRole() == DatabaseRole.MASTER;
    }

    @Override
    public ConnectionString getConnectionString() {
        return this;
    }

    @Override
    public String getPrimaryConnectionUrl() {
        return primaryConnectionUrl;
    }

    @Override
    public String getFailOverConnectionUrl() {
        return failOverConnectionUrl;
    }

    @Override
    public String getUsername() {
        return databaseConfig.getUid();
    }

    @Override
    public String getPassword() {
        return databaseConfig.getPwd();
    }

    protected DatabaseConfigImpl getDatabaseConfig() {
        return databaseConfig;
    }

    protected String getPrimaryHost() {
        return databaseConfig.getIp();
    }

    protected Integer getPrimaryPort() {
        return databaseConfig.getPort();
    }

    protected String getFailOverHost() {
        DatabaseShardConfigImpl databaseShardConfig = databaseConfig.getDatabaseShardConfig();
        return isMaster() ? databaseShardConfig.getMasterDomain() : databaseShardConfig.getSlaveDomain();
    }

    protected Integer getFailOverPort() {
        DatabaseShardConfigImpl databaseShardConfig = databaseConfig.getDatabaseShardConfig();
        Integer port =  isMaster() ? databaseShardConfig.getMasterPort() : databaseShardConfig.getSlavePort();
        return port != null ? port : getPrimaryPort();
    }

}
