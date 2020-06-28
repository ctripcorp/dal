package com.ctrip.platform.dal.daogen.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taochen on 2019/7/26.
 */
public class TitanKeyInfoReportDto {
    private int titanKeyCount;

    private int useMysqlCount;

    private int useSqlServerCount;

    private int directConnectDBCount;

    private int directConnectMysqlCount;

    private int directConnectSqlServerCount;

    private String statisticsDate;

    private List<AbnormalTitanKey> abnormalTitanKeyList = new ArrayList<>();

    private List<TitanKeyAPIInfo> unUseDynamicDSTitanKey = new ArrayList<>();

    public int getTitanKeyCount() {
        return titanKeyCount;
    }

    public void setTitanKeyCount(int titanKeyCount) {
        this.titanKeyCount = titanKeyCount;
    }

    public int getUseMysqlCount() {
        return useMysqlCount;
    }

    public void setUseMysqlCount(int useMysqlCount) {
        this.useMysqlCount = useMysqlCount;
    }

    public int getUseSqlServerCount() {
        return useSqlServerCount;
    }

    public void setUseSqlServerCount(int useSqlServerCount) {
        this.useSqlServerCount = useSqlServerCount;
    }

    public int getDirectConnectMysqlCount() {
        return directConnectMysqlCount;
    }

    public void setDirectConnectMysqlCount(int directConnectMysqlCount) {
        this.directConnectMysqlCount = directConnectMysqlCount;
    }

    public int getDirectConnectSqlServerCount() {
        return directConnectSqlServerCount;
    }

    public void setDirectConnectSqlServerCount(int directConnectSqlServerCount) {
        this.directConnectSqlServerCount = directConnectSqlServerCount;
    }

    public int getDirectConnectDBCount() {
        return directConnectDBCount;
    }

    public void setDirectConnectDBCount(int directConnectDBCount) {
        this.directConnectDBCount = directConnectDBCount;
    }

    public List<AbnormalTitanKey> getAbnormalTitanKeyList() {
        return abnormalTitanKeyList;
    }

    public void setAbnormalTitanKeyList(List<AbnormalTitanKey> abnormalTitanKeyList) {
        this.abnormalTitanKeyList = abnormalTitanKeyList;
    }

    public String getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(String statisticsDate) {
        this.statisticsDate = statisticsDate;
    }

    public List<TitanKeyAPIInfo> getUnUseDynamicDSTitanKey() {
        return unUseDynamicDSTitanKey;
    }

    public void setUnUseDynamicDSTitanKey(List<TitanKeyAPIInfo> unUseDynamicDSTitanKey) {
        this.unUseDynamicDSTitanKey = unUseDynamicDSTitanKey;
    }
}
