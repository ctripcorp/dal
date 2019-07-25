package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.*;
import java.sql.Types;

/**
 * Created by taochen on 2019/7/19.
 */
@Entity
@Database(name = "dao")
@Table(name = "dynamicdsswitchdata")
public class TitanKeySwitchInfoDB {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "titanKey")
    @Type(value = Types.VARCHAR)
    private String titanKey;

    @Column(name = "appIDCount")
    @Type(value = Types.INTEGER)
    private Integer appIDCount;

    @Column(name = "switchCount")
    @Type(value = Types.INTEGER)
    private Integer switchCount;

    @Column(name = "ipCount")
    @Type(value = Types.INTEGER)
    private Integer ipCount;

    @Column(name = "checkTime")
    @Type(value = Types.INTEGER)
    private Integer checkTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitanKey() {
        return titanKey;
    }

    public void setTitanKey(String titanKey) {
        this.titanKey = titanKey;
    }

    public Integer getAppIDCount() {
        return appIDCount;
    }

    public void setAppIDCount(Integer appIDCount) {
        this.appIDCount = appIDCount;
    }

    public Integer getSwitchCount() {
        return switchCount;
    }

    public void setSwitchCount(Integer switchCount) {
        this.switchCount = switchCount;
    }

    public Integer getIpCount() {
        return ipCount;
    }

    public void setIpCount(Integer ipCount) {
        this.ipCount = ipCount;
    }

    public Integer getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Integer checkTime) {
        this.checkTime = checkTime;
    }
}
