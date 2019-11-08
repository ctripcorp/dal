package com.ctrip.framework.db.cluster.entity;

import java.util.List;

/**
 * Created by taochen on 2019/11/6.
 */
public class Cluster {
    private String clusterName;

    private String dbCategory;

    private List<Zone> zones;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbCategory() {
        return dbCategory;
    }

    public void setDbCategory(String dbCategory) {
        this.dbCategory = dbCategory;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}
