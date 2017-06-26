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
@Table(name = "group_relation")
public class GroupRelation implements DalPojo {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "current_group_id")
    @Type(value = Types.INTEGER)
    private Integer currentGroupId;

    @Column(name = "child_group_id")
    @Type(value = Types.INTEGER)
    private Integer childGroupId;

    // 子类组的角色，1：当前组的管理员，2：受限用户
    @Column(name = "child_group_role")
    @Type(value = Types.INTEGER)
    private Integer childGroupRole = 2;

    @Column(name = "adduser")
    @Type(value = Types.INTEGER)
    private Integer adduser = 2;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String updateUserNo;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrentGroupId() {
        return currentGroupId;
    }

    public void setCurrentGroupId(Integer currentGroupId) {
        this.currentGroupId = currentGroupId;
    }

    public Integer getChildGroupId() {
        return childGroupId;
    }

    public void setChildGroupId(Integer childGroupId) {
        this.childGroupId = childGroupId;
    }

    public Integer getChildGroupRole() {
        return childGroupRole;
    }

    public void setChildGroupRole(Integer childGroupRole) {
        this.childGroupRole = childGroupRole;
    }

    public Integer getAdduser() {
        return adduser;
    }

    public void setAdduser(Integer adduser) {
        this.adduser = adduser;
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

}
