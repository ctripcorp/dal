package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.shard.DatabaseShard;
import com.ctrip.framework.dal.cluster.client.shard.DatabaseShardImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * @author c7ch23en
 */
public class DatabaseShardConfigImpl implements DatabaseShardConfig {

    private static final String ALIAS_KEYS_SPLITTER = ",";

    private ClusterConfigImpl clusterConfig;
    private int shardIndex;
    private String zone;
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
    public DatabaseShard generate() {
        DatabaseShardImpl databaseShard = new DatabaseShardImpl(this);
        for (DatabaseConfig databaseConfig : databaseConfigs)
            databaseShard.addDatabase(databaseConfig.generate());
        return databaseShard;
    }

    public ClusterConfigImpl getClusterConfig() {
        return clusterConfig;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public String getZone() {
        return zone;
    }

    public String getMasterDomain() {
        return masterDomain;
    }

    public Integer getMasterPort() {
        return masterPort;
    }

    public String[] getMasterKeys() {
        return masterKeys != null ? masterKeys.split(ALIAS_KEYS_SPLITTER) : null;
    }

    public String getSlaveDomain() {
        return slaveDomain;
    }

    public Integer getSlavePort() {
        return slavePort;
    }

    public String[] getSlaveKeys() {
        return slaveKeys != null ? slaveKeys.split(ALIAS_KEYS_SPLITTER) : null;
    }

    public void setZone(String zone) {
        this.zone = zone;
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
