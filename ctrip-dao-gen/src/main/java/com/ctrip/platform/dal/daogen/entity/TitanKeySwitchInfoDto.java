package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/11.
 */
public class TitanKeySwitchInfoDto {
    private String titanKey;

    private List<SwitchCountTime> switchs;

    private List<AppIDSwitchInfoDto> appIDList;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public List<SwitchCountTime> getSwitchs() {
        return switchs;
    }

    public void setSwitchs(List<SwitchCountTime> switchs) {
        this.switchs = switchs;
    }

    public List<AppIDSwitchInfoDto> getAppIDList() {
        return appIDList;
    }

    public void setAppIDList(List<AppIDSwitchInfoDto> appIDList) {
        this.appIDList = appIDList;
    }
}
