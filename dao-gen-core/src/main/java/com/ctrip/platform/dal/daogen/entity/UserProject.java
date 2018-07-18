package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Types;

@Entity
@Database(name = "dao")
@Table(name = "user_project")
public class UserProject implements DalPojo {
    @Column(name = "id")
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "project_id")
    @Type(value = Types.INTEGER)
    private Integer project_id;

    @Column(name = "user_no")
    @Type(value = Types.VARCHAR)
    private String userNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProject_id() {
        return project_id;
    }

    public void setProject_id(Integer project_id) {
        this.project_id = project_id;
    }

    public String getUser_no() {
        return userNo;
    }

    public void setUser_no(String user_no) {
        this.userNo = user_no;
    }
}
