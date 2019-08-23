package com.ctrip.framework.dal.cluster.client.config;

import com.ctrip.framework.dal.cluster.client.database.Database;
import com.ctrip.framework.dal.cluster.client.database.DatabaseCategory;
import com.ctrip.framework.dal.cluster.client.database.DatabaseRole;
import com.ctrip.framework.dal.cluster.client.database.MySqlDatabase;
import com.ctrip.framework.dal.cluster.client.exception.ClusterConfigException;
import com.ctrip.framework.dal.cluster.client.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author c7ch23en
 */
public class DatabaseConfigImpl implements DatabaseConfig {

    private DatabaseShardConfigImpl databaseShardConfig;
    private DatabaseRole role = DatabaseRole.MASTER;
    private String ip;
    private Integer port;
    private String dbName;
    private String uid;
    private String pwd;
    private String charset = "UTF-8";
    private Integer readWeight = 1;
    private Set<String> tags = new HashSet<>();

    public DatabaseConfigImpl(DatabaseShardConfigImpl databaseShardConfig) {
        this.databaseShardConfig = databaseShardConfig;
    }

    @Override
    public Database generate() {
        DatabaseCategory databaseCategory = databaseShardConfig.getClusterConfig().getDatabaseCategory();
        if (databaseCategory == null)
            throw new ClusterConfigException("undefined database category");
        else if (databaseCategory == DatabaseCategory.MYSQL)
            return new MySqlDatabase(this);
        else
            throw new ClusterConfigException(String.format("unsupported database category: %s", databaseCategory.getValue()));
    }

    public DatabaseShardConfigImpl getDatabaseShardConfig() {
        return databaseShardConfig;
    }

    public DatabaseRole getRole() {
        return role;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public String getUid() {
        return uid;
    }

    public String getPwd() {
        return pwd;
    }

    public Integer getReadWeight() {
        return readWeight;
    }

    public void setRole(DatabaseRole role) {
        this.role = role;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setReadWeight(Integer readWeight) {
        this.readWeight = readWeight;
    }

    public void setTags(String tags) {
        if (!StringUtils.isEmpty(tags)) {
            String[] arrayTags = tags.split(",");
            this.tags.addAll(Arrays.asList(arrayTags));
        }
    }

}
