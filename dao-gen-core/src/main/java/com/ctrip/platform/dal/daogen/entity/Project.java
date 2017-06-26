package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "dao")
@Table(name = "project")
public class Project implements DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "name")
    @Type(value = Types.VARCHAR)
    private String name;

    @Column(name = "namespace")
    @Type(value = Types.VARCHAR)
    private String namespace;

    @Column(name = "dal_group_id")
    @Type(value = Types.INTEGER)
    private Integer dalGroupId;

    @Column(name = "dal_config_name")
    @Type(value = Types.VARCHAR)
    private String dalConfigName;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String updateUserNo;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

    @Column(name = "lang")
    @Type(value = Types.VARCHAR)
    private String lang;

    private String str_update_time;

    private String text;

    private String icon;

    private boolean children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Integer getDalGroupId() {
        return dalGroupId;
    }

    public void setDalGroupId(Integer dalGroupId) {
        this.dalGroupId = dalGroupId;
    }

    public String getDalConfigName() {
        return dalConfigName;
    }

    public void setDalConfigName(String dalConfigName) {
        this.dalConfigName = dalConfigName;
    }

    public String getUpdateUserNo() {
        return updateUserNo;
    }

    public void setUpdateUserNo(String updateUserNo) {
        this.updateUserNo = updateUserNo;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getStr_update_time() {
        return str_update_time;
    }

    public void setStr_update_time(String str_update_time) {
        this.str_update_time = str_update_time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

}
