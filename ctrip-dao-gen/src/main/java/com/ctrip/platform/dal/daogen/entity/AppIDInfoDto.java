package com.ctrip.platform.dal.daogen.entity;

import java.util.Map;

/**
 * Created by taochen on 2019/7/9.
 */
public class AppIDInfoDto {
    private String appID;

//    private String hostIPs;
//
//    private String hostSuccessCount;
//
//    private String hostSwitchCount;

    private int hostIPCount;

    private int appIDSwitchCount;

    private int appIDSuccessCount;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

//    public String getHostIPs() {
//        return hostIPs;
//    }
//
//    public void setHostIPs(String hostIPs) {
//        this.hostIPs = hostIPs;
//    }
//
//    public String getHostSuccessCount() {
//        return hostSuccessCount;
//    }
//
//    public void setHostSuccessCount(String hostSuccessCount) {
//        this.hostSuccessCount = hostSuccessCount;
//    }
//
//    public String getHostSwitchCount() {
//        return hostSwitchCount;
//    }
//
//    public void setHostSwitchCount(String hostSwitchCount) {
//        this.hostSwitchCount = hostSwitchCount;
//    }


    public int getHostIPCount() {
        return hostIPCount;
    }

    public void setHostIPCount(int hostIPCount) {
        this.hostIPCount = hostIPCount;
    }

    public int getAppIDSwitchCount() {
        return appIDSwitchCount;
    }

    public void setAppIDSwitchCount(int appIDSwitchCount) {
        this.appIDSwitchCount = appIDSwitchCount;
    }

    public int getAppIDSuccessCount() {
        return appIDSuccessCount;
    }

    public void setAppIDSuccessCount(int appIDSuccessCount) {
        this.appIDSuccessCount = appIDSuccessCount;
    }
}
