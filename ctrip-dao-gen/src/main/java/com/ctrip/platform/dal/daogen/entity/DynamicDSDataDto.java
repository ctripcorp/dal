package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/5.
 */
public class DynamicDSDataDto {
    private String titanKey;

    private String titanKeySwitchCount;

    private List<AppIDInfoDto> appIds;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public String getTitanKeySwitchCount() {
        return titanKeySwitchCount;
    }

    public void setTitanKeySwitchCount(String titanKeySwitchCount) {
        this.titanKeySwitchCount = titanKeySwitchCount;
    }

    public List<AppIDInfoDto> getAppIds() {
        return appIds;
    }

    public void setAppIds(List<AppIDInfoDto> appIds) {
        this.appIds = appIds;
    }
}
