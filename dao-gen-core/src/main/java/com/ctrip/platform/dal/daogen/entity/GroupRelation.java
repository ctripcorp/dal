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
    private Integer current_group_id;

    @Column(name = "child_group_id")
    @Type(value = Types.INTEGER)
    private Integer child_group_id;

    // 子类组的角色，1：当前组的管理员，2：受限用户
    @Column(name = "child_group_role")
    @Type(value = Types.INTEGER)
    private Integer child_group_role = 2;

    @Column(name = "adduser")
    @Type(value = Types.INTEGER)
    private Integer adduser = 2;

    @Column(name = "update_user_no")
    @Type(value = Types.VARCHAR)
    private String update_user_no;

    @Column(name = "update_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp update_time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCurrent_group_id() {
        return current_group_id;
    }

    public void setCurrent_group_id(Integer current_group_id) {
        this.current_group_id = current_group_id;
    }

    public Integer getChild_group_id() {
        return child_group_id;
    }

    public void setChild_group_id(Integer child_group_id) {
        this.child_group_id = child_group_id;
    }

    public Integer getChild_group_role() {
        return child_group_role;
    }

    public void setChild_group_role(Integer child_group_role) {
        this.child_group_role = child_group_role;
    }

    public Integer getAdduser() {
        return adduser;
    }

    public void setAdduser(Integer adduser) {
        this.adduser = adduser;
    }

    public String getUpdate_user_no() {
        return update_user_no;
    }

    public void setUpdate_user_no(String update_user_no) {
        this.update_user_no = update_user_no;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

}
