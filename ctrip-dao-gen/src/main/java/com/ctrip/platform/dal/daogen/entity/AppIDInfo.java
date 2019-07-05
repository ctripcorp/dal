package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/3.
 */
public class AppIDInfo {
    private String appID;

    private List<SwitchHostIPInfo> hostIPInfolist;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public List<SwitchHostIPInfo> getHostIPInfolist() {
        return hostIPInfolist;
    }

    public void setHostIPInfolist(List<SwitchHostIPInfo> hostIPInfolist) {
        this.hostIPInfolist = hostIPInfolist;
    }
}
