package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyAPIInfo {
    private String name;

    private String subEnv;

    private String providerName;

    private ConnectionInfo connectionInfo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubEnv() {
        return subEnv;
    }

    public void setSubEnv(String subEnv) {
        this.subEnv = subEnv;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }
}
