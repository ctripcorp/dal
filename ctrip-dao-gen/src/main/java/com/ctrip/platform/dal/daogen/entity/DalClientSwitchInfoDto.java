package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/11.
 */
public class DalClientSwitchInfoDto {
    private String dalClientIP;

    private List<SwitchCountTime> startSwitchs;

    private List<SwitchCountTime> endSwitchs;

    public String getDalClientIP() {
        return dalClientIP;
    }

    public void setDalClientIP(String dalClientIP) {
        this.dalClientIP = dalClientIP;
    }

    public List<SwitchCountTime> getStartSwitchs() {
        return startSwitchs;
    }

    public void setStartSwitchs(List<SwitchCountTime> startSwitchs) {
        this.startSwitchs = startSwitchs;
    }

    public List<SwitchCountTime> getEndSwitchs() {
        return endSwitchs;
    }

    public void setEndSwitchs(List<SwitchCountTime> endSwitchs) {
        this.endSwitchs = endSwitchs;
    }
}
