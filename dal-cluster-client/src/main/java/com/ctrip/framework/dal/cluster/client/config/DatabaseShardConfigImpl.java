package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.shard.DatabaseShard;
import com.ctrip.framework.dal.cluster.client.shard.DatabaseShardImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class DatabaseShardConfigImpl implements DatabaseShardConfig {

    private ClusterConfigImpl clusterConfig;
    private int shardIndex;
    private String masterDomain;
    private Integer masterPort;
    private String masterKeys;
    private String slaveDomain;
    private Integer slavePort;
    private String slaveKeys;
    private List<DatabaseConfig> databaseConfigs = new LinkedList<>();

    public DatabaseShardConfigImpl(ClusterConfigImpl clusterConfig, int shardIndex) {
        this.clusterConfig = clusterConfig;
        this.shardIndex = shardIndex;
    }

    @Override
    public DatabaseShard generateDatabaseShard() {
        DatabaseShardImpl databaseShard = new DatabaseShardImpl(this);
        for (DatabaseConfig databaseConfig : databaseConfigs)
            databaseShard.addDatabase(databaseConfig.generateDatabase());
        return databaseShard;
    }

    public ClusterConfigImpl getClusterConfig() {
        return clusterConfig;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public String getMasterDomain() {
        return masterDomain;
    }

    public Integer getMasterPort() {
        return masterPort;
    }

    public String getMasterKeys() {
        return masterKeys;
    }

    public String getSlaveDomain() {
        return slaveDomain;
    }

    public Integer getSlavePort() {
        return slavePort;
    }

    public String getSlaveKeys() {
        return slaveKeys;
    }

    public void setMasterDomain(String masterDomain) {
        this.masterDomain = masterDomain;
    }

    public void setMasterPort(Integer masterPort) {
        this.masterPort = masterPort;
    }

    public void setMasterKeys(String masterKeys) {
        this.masterKeys = masterKeys;
    }

    public void setSlaveDomain(String slaveDomain) {
        this.slaveDomain = slaveDomain;
    }

    public void setSlavePort(Integer slavePort) {
        this.slavePort = slavePort;
    }

    public void setSlaveKeys(String slaveKeys) {
        this.slaveKeys = slaveKeys;
    }

    public void addDatabaseConfig(DatabaseConfig databaseConfig) {
        databaseConfigs.add(databaseConfig);
    }

}
