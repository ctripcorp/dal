package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/9.
 */
public class AppIDInfoDto {
    private String appID;

    private String hostIPs;

    private String hostSuccessCount;

    private String hostSwitchCount;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getHostIPs() {
        return hostIPs;
    }

    public void setHostIPs(String hostIPs) {
        this.hostIPs = hostIPs;
    }

    public String getHostSuccessCount() {
        return hostSuccessCount;
    }

    public void setHostSuccessCount(String hostSuccessCount) {
        this.hostSuccessCount = hostSuccessCount;
    }

    public String getHostSwitchCount() {
        return hostSwitchCount;
    }

    public void setHostSwitchCount(String hostSwitchCount) {
        this.hostSwitchCount = hostSwitchCount;
    }
}
