package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/11.
 */
public class TitanKeySwitchInfoDto {
    private String titanKey;

    private List<SwitchCountTime> switches;

    //private List<AppIDSwitchInfoDto> appIDList;

    private List<SwitchAppIDInfoDto> appIDList;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public List<SwitchCountTime> getSwitches() {
        return switches;
    }

    public void setSwitches(List<SwitchCountTime> switches) {
        this.switches = switches;
    }

//    public List<AppIDSwitchInfoDto> getAppIDList() {
//        return appIDList;
//    }
//
//    public void setAppIDList(List<AppIDSwitchInfoDto> appIDList) {
//        this.appIDList = appIDList;
//    }


    public List<SwitchAppIDInfoDto> getAppIDList() {
        return appIDList;
    }

    public void setAppIDList(List<SwitchAppIDInfoDto> appIDList) {
        this.appIDList = appIDList;
    }
}
