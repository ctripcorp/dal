package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/11.
 */
public class DalClientSwitchInfoDto {
    private String dalClientIP;

    private List<SwitchCountTime> switchs;

    public String getDalClientIP() {
        return dalClientIP;
    }

    public void setDalClientIP(String dalClientIP) {
        this.dalClientIP = dalClientIP;
    }

    public List<SwitchCountTime> getSwitchs() {
        return switchs;
    }

    public void setSwitchs(List<SwitchCountTime> switchs) {
        this.switchs = switchs;
    }
}
