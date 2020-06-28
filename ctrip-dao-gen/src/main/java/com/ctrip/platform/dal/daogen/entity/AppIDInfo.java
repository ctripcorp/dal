package com.ctrip.platform.dal.daogen.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by taochen on 2019/7/3.
 */
public class AppIDInfo {
    private String appID;

    //private List<SwitchHostIPInfo> hostIPInfolist;

    private List<String> hostIPInfolist;

    private Map<Integer, Integer> appIDSwitchTime;

    private Map<Integer, Integer> appIDSuccessTime;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public List<String> getHostIPInfolist() {
        return hostIPInfolist;
    }

    public void setHostIPInfolist(List<String> hostIPInfolist) {
        this.hostIPInfolist = hostIPInfolist;
    }

    public Map<Integer, Integer> getAppIDSwitchTime() {
        return appIDSwitchTime;
    }

    public void setAppIDSwitchTime(Map<Integer, Integer> appIDSwitchTime) {
        this.appIDSwitchTime = appIDSwitchTime;
    }

    public Map<Integer, Integer> getAppIDSuccessTime() {
        return appIDSuccessTime;
    }

    public void setAppIDSuccessTime(Map<Integer, Integer> appIDSuccessTime) {
        this.appIDSuccessTime = appIDSuccessTime;
    }
}
