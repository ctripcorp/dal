package com.ctrip.platform.dal.daogen.entity;

import java.util.List;

/**
 * Created by taochen on 2019/7/16.
 */
public class CheckDynamicDSResponse {

    /**
     * 0、统计完成 1、当前查询时间数据正在统计中 2、正在统计其他时间数据
     */
    private int statusCode;

    private int switchTitanKeyCount;

    private int statisticProgress;

    private String statisticTime;

    private List<DynamicDSDataDto> dynamicDSDataList;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getSwitchTitanKeyCount() {
        return switchTitanKeyCount;
    }

    public void setSwitchTitanKeyCount(int switchTitanKeyCount) {
        this.switchTitanKeyCount = switchTitanKeyCount;
    }

    public int getStatisticProgress() {
        return statisticProgress;
    }

    public void setStatisticProgress(int statisticProgress) {
        this.statisticProgress = statisticProgress;
    }

    public String getStatisticTime() {
        return statisticTime;
    }

    public void setStatisticTime(String statisticTime) {
        this.statisticTime = statisticTime;
    }

    public List<DynamicDSDataDto> getDynamicDSDataList() {
        return dynamicDSDataList;
    }

    public void setDynamicDSDataList(List<DynamicDSDataDto> dynamicDSDataList) {
        this.dynamicDSDataList = dynamicDSDataList;
    }
}
