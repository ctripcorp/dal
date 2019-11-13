package com.ctrip.framework.dal.dbconfig.plugin.entity.dal;

import java.util.List;

/**
 * Created by shenjie on 2019/4/18.
 */
public class DatabaseShardInfo {
    private Integer index;
    private String masterDomain;
    private String slaveDomain;
    private Integer masterPort;
    private Integer slavePort;
    private String masterTitanKeys;
    private String slaveTitanKeys;
    private List<DatabaseInfo> databases;

    public DatabaseShardInfo() {
    }

    public DatabaseShardInfo(Integer index, String masterDomain, String slaveDomain, Integer masterPort, Integer slavePort, String masterTitanKeys, String slaveTitanKeys, List<DatabaseInfo> databases) {
        this.index = index;
        this.masterDomain = masterDomain;
        this.slaveDomain = slaveDomain;
        this.masterPort = masterPort;
        this.slavePort = slavePort;
        this.masterTitanKeys = masterTitanKeys;
        this.slaveTitanKeys = slaveTitanKeys;
        this.databases = databases;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getMasterDomain() {
        return masterDomain;
    }

    public void setMasterDomain(String masterDomain) {
        this.masterDomain = masterDomain;
    }

    public String getSlaveDomain() {
        return slaveDomain;
    }

    public void setSlaveDomain(String slaveDomain) {
        this.slaveDomain = slaveDomain;
    }

    public Integer getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(Integer masterPort) {
        this.masterPort = masterPort;
    }

    public Integer getSlavePort() {
        return slavePort;
    }

    public void setSlavePort(Integer slavePort) {
        this.slavePort = slavePort;
    }

    public String getMasterTitanKeys() {
        return masterTitanKeys;
    }

    public void setMasterTitanKeys(String masterTitanKeys) {
        this.masterTitanKeys = masterTitanKeys;
    }

    public String getSlaveTitanKeys() {
        return slaveTitanKeys;
    }

    public void setSlaveTitanKeys(String slaveTitanKeys) {
        this.slaveTitanKeys = slaveTitanKeys;
    }

    public List<DatabaseInfo> getDatabases() {
        return databases;
    }

    public void setDatabases(List<DatabaseInfo> databases) {
        this.databases = databases;
    }

    @Override
    public String toString() {
        return "DatabaseShardInfo{" +
                "index=" + index +
                ", masterDomain='" + masterDomain + '\'' +
                ", slaveDomain='" + slaveDomain + '\'' +
                ", masterPort=" + masterPort +
                ", slavePort=" + slavePort +
                ", masterTitanKey='" + masterTitanKeys + '\'' +
                ", slaveTitanKey='" + slaveTitanKeys + '\'' +
                ", databases=" + databases +
                '}';
    }
}
