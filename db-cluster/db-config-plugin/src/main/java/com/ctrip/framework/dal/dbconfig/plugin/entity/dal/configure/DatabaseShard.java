package com.ctrip.framework.dal.dbconfig.plugin.entity.dal.configure;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by shenjie on 2019/5/7.
 */
@XmlAccessorType(XmlAccessType.FIELD)
// XML文件中的根标识
@XmlRootElement(name = "DatabaseShard")
public class DatabaseShard {

    @XmlAttribute(name = "index")
    private int index;
    @XmlAttribute(name = "masterDomain")
    private String masterDomain;
    @XmlAttribute(name = "masterPort")
    private Integer masterPort;
    @XmlAttribute(name = "masterTitanKeys")
    private String masterTitanKeys;
    @XmlAttribute(name = "slaveDomain")
    private String slaveDomain;
    @XmlAttribute(name = "slavePort")
    private Integer slavePort;
    @XmlAttribute(name = "slaveTitanKeys")
    private String slaveTitanKeys;
    @XmlElement(name = "Database")
    private List<Database> databases;

    public DatabaseShard() {
    }

    public DatabaseShard(int index, String masterDomain, String slaveDomain, Integer masterPort, Integer slavePort, String masterTitanKeys, String slaveTitanKeys) {
        this.index = index;
        this.masterDomain = masterDomain;
        this.slaveDomain = slaveDomain;
        this.masterPort = masterPort;
        this.slavePort = slavePort;
        this.masterTitanKeys = masterTitanKeys;
        this.slaveTitanKeys = slaveTitanKeys;
    }

    public DatabaseShard(int index, String masterDomain, String slaveDomain, Integer masterPort, Integer slavePort, String masterTitanKeys, String slaveTitanKeys, List<Database> databases) {
        this.index = index;
        this.masterDomain = masterDomain;
        this.slaveDomain = slaveDomain;
        this.masterPort = masterPort;
        this.slavePort = slavePort;
        this.masterTitanKeys = masterTitanKeys;
        this.slaveTitanKeys = slaveTitanKeys;
        this.databases = databases;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
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

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }

    @Override
    public String toString() {
        return "DatabaseShard{" +
                "index=" + index +
                ", masterDomain='" + masterDomain + '\'' +
                ", slaveDomain='" + slaveDomain + '\'' +
                ", masterPort='" + masterPort + '\'' +
                ", slavePort='" + slavePort + '\'' +
                ", masterKeys='" + masterTitanKeys + '\'' +
                ", slaveKeys='" + slaveTitanKeys + '\'' +
                ", databases=" + databases +
                '}';
    }
}
