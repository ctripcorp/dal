package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/26.
 */
public class AbnormalTitanKey {
    private String titanKey;

    private String serverIp;

    private String serverName;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
