package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/5.
 */
public class DynamicDSDataDto {
    private String titanKey;

    private String titanKeySwitchCount;

    private String appIds;

    private String hostIps;

    private String hostSwitchCount;

    private String hostSuccessCount;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public String getAppIds() {
        return appIds;
    }

    public void setAppIds(String appIds) {
        this.appIds = appIds;
    }

    public String getHostIps() {
        return hostIps;
    }

    public void setHostIps(String hostIps) {
        this.hostIps = hostIps;
    }

    public String getTitanKeySwitchCount() {
        return titanKeySwitchCount;
    }

    public void setTitanKeySwitchCount(String titanKeySwitchCount) {
        this.titanKeySwitchCount = titanKeySwitchCount;
    }

    public String getHostSwitchCount() {
        return hostSwitchCount;
    }

    public void setHostSwitchCount(String hostSwitchCount) {
        this.hostSwitchCount = hostSwitchCount;
    }

    public String getHostSuccessCount() {
        return hostSuccessCount;
    }

    public void setHostSuccessCount(String hostSuccessCount) {
        this.hostSuccessCount = hostSuccessCount;
    }
}
