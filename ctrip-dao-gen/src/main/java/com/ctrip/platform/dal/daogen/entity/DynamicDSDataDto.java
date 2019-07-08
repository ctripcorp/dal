package com.ctrip.platform.dal.daogen.entity;

/**
 * Created by taochen on 2019/7/5.
 */
public class DynamicDSDataDto {
    private String titanKey;

    private String appIds;

    private String hostIps;

    private String switchCount;

    private String successCount;

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public String getAppIds() {
        return appIds;
    }

    public void setAppIds(String appIds) {
        this.appIds = appIds;
    }

    public String getHostIps() {
        return hostIps;
    }

    public void setHostIps(String hostIps) {
        this.hostIps = hostIps;
    }

    public String getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(String switchCount) {
        this.switchCount = switchCount;
    }

    public String getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(String successCount) {
        this.successCount = successCount;
    }
}
