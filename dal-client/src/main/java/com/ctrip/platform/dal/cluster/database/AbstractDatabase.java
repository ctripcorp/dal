package com.ctrip.platform.dal.cluster.database;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.config.DatabaseConfigImpl;
import com.ctrip.platform.dal.cluster.config.DatabaseShardConfigImpl;

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
        return databaseConfig.getRole() == DatabaseRole.MASTER;
    }

    @Override
    public String getZone() {
        return databaseConfig.getZone();
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

    @Override
    public String getDbName() {
        return databaseConfig.getDbName();
    }

    @Override
    public String getPrimaryHost() {
        return databaseConfig.getIp();
    }

    @Override
    public int getPrimaryPort() {
        return databaseConfig.getPort();
    }

    @Override
    public String getClusterName() {
        return databaseConfig.getDatabaseShardConfig().getClusterConfig().getClusterName();
    }

    @Override
    public int getShardIndex() {
        return databaseConfig.getDatabaseShardConfig().getShardIndex();
    }

    @Override
    public String[] getAliasKeys() {
        DatabaseShardConfigImpl databaseShardConfig = databaseConfig.getDatabaseShardConfig();
        return isMaster() ? databaseShardConfig.getMasterKeys() : databaseShardConfig.getSlaveKeys();
    }

    @Override
    public Cluster getCluster() {
        return databaseConfig.getDatabaseShardConfig().getClusterConfig().getOrCreateCluster();
    }

    protected DatabaseConfigImpl getDatabaseConfig() {
        return databaseConfig;
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
