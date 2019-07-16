package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/15.
 */
public class SwitchAppIDInfoDto {
    private String appID;

    private List<SwitchCountTime> switches;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public List<SwitchCountTime> getSwitches() {
        return switches;
    }

    public void setSwitches(List<SwitchCountTime> switches) {
        this.switches = switches;
    }
}
