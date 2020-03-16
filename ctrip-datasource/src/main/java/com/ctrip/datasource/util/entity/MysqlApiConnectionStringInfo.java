package com.ctrip.datasource.util.entity;

import java.util.List;

public class MysqlApiConnectionStringInfo {
    private String connectionstring;

    private List<ClusterNodeInfo> clusternodeinfolist;

    private String clustertype;

    public String getConnectionstring() {
        return connectionstring;
    }

    public void setConnectionstring(String connectionstring) {
        this.connectionstring = connectionstring;
    }

    public List<ClusterNodeInfo> getClusternodeinfolist() {
        return clusternodeinfolist;
    }

    public void setClusternodeinfolist(List<ClusterNodeInfo> clusternodeinfolist) {
        this.clusternodeinfolist = clusternodeinfolist;
    }

    public String getClustertype() {
        return clustertype;
    }

    public void setClustertype(String clustertype) {
        this.clustertype = clustertype;
    }
}
