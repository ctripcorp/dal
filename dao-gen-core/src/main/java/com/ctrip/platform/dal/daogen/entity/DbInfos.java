package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.sql.Types;

public class DbInfos {

    private Integer id;

    private Integer dbId;

    private String dbNameBase;

    private String dbName;

    private String dbType;

    private Integer isShard;

    private Integer shardCount;

    private Integer shardGroupId;

    private Integer organizationId;

    private String organizationName;

    private Integer productlineId;

    private String productlineName;

    private Integer productId;

    private String productName;

    private String dbStatus;

    private Integer needRead;

    @Type(value = Types.VARCHAR)
    private String description;

    private Integer hasRds;

    private Integer isCore;

    private String schemaApprovers;

    private String createBy;

    private Integer level;

    private String teamOwners;

    private String modifyBy;

    private Integer isDrc;

    private String dbOwners;

    private Integer hasDr;

    private String prodClusters;

    private Timestamp insertTime;

    private Timestamp modifyTime;

    private Timestamp createTime;

    private Timestamp datachangeLasttime;

    private String fatClusters;

    private String uatClusters;

    private String lptClusters;

    private Integer hasDalCluster;

    private Integer tableSharding;

    public Integer getTableSharding() {
        return tableSharding;
    }

    public void setTableSharding(Integer tableSharding) {
        this.tableSharding = tableSharding;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDbId() {
        return dbId;
    }

    public void setDbId(Integer dbId) {
        this.dbId = dbId;
    }

    public String getDbNameBase() {
        return dbNameBase;
    }

    public void setDbNameBase(String dbNameBase) {
        this.dbNameBase = dbNameBase;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Integer getIsShard() {
        return isShard;
    }

    public void setIsShard(Integer isShard) {
        this.isShard = isShard;
    }

    public Integer getShardCount() {
        return shardCount;
    }

    public void setShardCount(Integer shardCount) {
        this.shardCount = shardCount;
    }

    public Integer getShardGroupId() {
        return shardGroupId;
    }

    public void setShardGroupId(Integer shardGroupId) {
        this.shardGroupId = shardGroupId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Integer getProductlineId() {
        return productlineId;
    }

    public void setProductlineId(Integer productlineId) {
        this.productlineId = productlineId;
    }

    public String getProductlineName() {
        return productlineName;
    }

    public void setProductlineName(String productlineName) {
        this.productlineName = productlineName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus) {
        this.dbStatus = dbStatus;
    }

    public Integer getNeedRead() {
        return needRead;
    }

    public void setNeedRead(Integer needRead) {
        this.needRead = needRead;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHasRds() {
        return hasRds;
    }

    public void setHasRds(Integer hasRds) {
        this.hasRds = hasRds;
    }

    public Integer getIsCore() {
        return isCore;
    }

    public void setIsCore(Integer isCore) {
        this.isCore = isCore;
    }

    public String getSchemaApprovers() {
        return schemaApprovers;
    }

    public void setSchemaApprovers(String schemaApprovers) {
        this.schemaApprovers = schemaApprovers;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getTeamOwners() {
        return teamOwners;
    }

    public void setTeamOwners(String teamOwners) {
        this.teamOwners = teamOwners;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    public Integer getIsDrc() {
        return isDrc;
    }

    public void setIsDrc(Integer isDrc) {
        this.isDrc = isDrc;
    }

    public String getDbOwners() {
        return dbOwners;
    }

    public void setDbOwners(String dbOwners) {
        this.dbOwners = dbOwners;
    }

    public Integer getHasDr() {
        return hasDr;
    }

    public void setHasDr(Integer hasDr) {
        this.hasDr = hasDr;
    }

    public String getProdClusters() {
        return prodClusters;
    }

    public void setProdClusters(String prodClusters) {
        this.prodClusters = prodClusters;
    }

    public Timestamp getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Timestamp insertTime) {
        this.insertTime = insertTime;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getDatachangeLasttime() {
        return datachangeLasttime;
    }

    public void setDatachangeLasttime(Timestamp datachangeLasttime) {
        this.datachangeLasttime = datachangeLasttime;
    }

    public String getFatClusters() {
        return fatClusters;
    }

    public void setFatClusters(String fatClusters) {
        this.fatClusters = fatClusters;
    }

    public String getUatClusters() {
        return uatClusters;
    }

    public void setUatClusters(String uatClusters) {
        this.uatClusters = uatClusters;
    }

    public String getLptClusters() {
        return lptClusters;
    }

    public void setLptClusters(String lptClusters) {
        this.lptClusters = lptClusters;
    }

    public Integer getHasDalCluster() { return hasDalCluster; }

    public void setHasDalCluster(Integer hasDalCluster) { this.hasDalCluster = hasDalCluster; }
}
