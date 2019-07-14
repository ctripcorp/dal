package com.ctrip.platform.dal.daogen.entity;

import java.util.Map;

/**
 * Created by taochen on 2019/7/8.
 */
public class SwitchTitanKey {
    private String titanKey;

    private Map<Integer, Integer> switchCount;

    private String Permissions;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public Map<Integer, Integer> getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(Map<Integer, Integer> switchCount) {
        this.switchCount = switchCount;
    }

    public String getPermissions() {
        return Permissions;
    }

    public void setPermissions(String permissions) {
        Permissions = permissions;
    }
}
