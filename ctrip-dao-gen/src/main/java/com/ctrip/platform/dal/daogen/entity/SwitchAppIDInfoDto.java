package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/15.
 */
public class SwitchAppIDInfoDto {
    private String appID;

    private List<SwitchCountTime> startSwitches;

    private List<SwitchCountTime> endSwitches;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public List<SwitchCountTime> getStartSwitches() {
        return startSwitches;
    }

    public void setStartSwitches(List<SwitchCountTime> startSwitches) {
        this.startSwitches = startSwitches;
    }

    public List<SwitchCountTime> getEndSwitches() {
        return endSwitches;
    }

    public void setEndSwitches(List<SwitchCountTime> endSwitches) {
        this.endSwitches = endSwitches;
    }
}
