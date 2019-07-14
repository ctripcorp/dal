package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/11.
 */
public class DalClientSwitchInfoDto {
    private String clientIP;

    private List<SwitchCountTime> startSwitches;

    private List<SwitchCountTime> endSwitches;

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
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
