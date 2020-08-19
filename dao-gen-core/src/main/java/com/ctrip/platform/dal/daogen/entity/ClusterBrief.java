package com.ctrip.platform.dal.daogen.entity;

public class ClusterBrief {
    private String clusterName;

    private String type;

    private String zoneId;

    private Integer unitStrategyId;

    private String unitStrategyName;

    private String shardStrategies;

    private String idGenerators;

    private String dbCategory;

    private boolean enabled;

    private boolean released;

    private Integer releaseVersion;

    private String createTime;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getUnitStrategyId() {
        return unitStrategyId;
    }

    public void setUnitStrategyId(Integer unitStrategyId) {
        this.unitStrategyId = unitStrategyId;
    }

    public String getUnitStrategyName() {
        return unitStrategyName;
    }

    public void setUnitStrategyName(String unitStrategyName) {
        this.unitStrategyName = unitStrategyName;
    }

    public String getDbCategory() {
        return dbCategory;
    }

    public void setDbCategory(String dbCategory) {
        this.dbCategory = dbCategory;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public String getShardStrategies() {
        return shardStrategies;
    }

    public void setShardStrategies(String shardStrategies) {
        this.shardStrategies = shardStrategies;
    }

    public String getIdGenerators() {
        return idGenerators;
    }

    public void setIdGenerators(String idGenerators) {
        this.idGenerators = idGenerators;
    }

    public Integer getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(Integer releaseVersion) {
        this.releaseVersion = releaseVersion;
    }

}
