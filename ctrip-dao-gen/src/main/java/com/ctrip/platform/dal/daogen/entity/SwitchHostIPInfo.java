package com.ctrip.platform.dal.daogen.entity;

import java.util.Map;

/**
 * Created by taochen on 2019/7/4.
 */
public class SwitchHostIPInfo {
    private String hostIP;

    private Map<Integer, Integer> startSwitchPoint;

    private Map<Integer, Integer> endSwitchPoint;

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public Map<Integer, Integer> getStartSwitchPoint() {
        return startSwitchPoint;
    }

    public void setStartSwitchPoint(Map<Integer, Integer> startSwitchPoint) {
        this.startSwitchPoint = startSwitchPoint;
    }

    public Map<Integer, Integer> getEndSwitchPoint() {
        return endSwitchPoint;
    }

    public void setEndSwitchPoint(Map<Integer, Integer> endSwitchPoint) {
        this.endSwitchPoint = endSwitchPoint;
    }
}
