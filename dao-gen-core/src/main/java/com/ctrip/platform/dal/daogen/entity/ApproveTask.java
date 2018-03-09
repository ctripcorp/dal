package com.ctrip.platform.dal.daogen.entity;

import com.ctrip.platform.dal.dao.DalPojo;
import com.ctrip.platform.dal.dao.annotation.Database;
import com.ctrip.platform.dal.dao.annotation.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.sql.Types;

@Entity
@Database(name = "dao")
@Table(name = "approve_task")
public class ApproveTask implements DalPojo {
    @Column(name = "id")
    @Type(value = Types.INTEGER)
    private Integer id;

    @Column(name = "task_id")
    @Type(value = Types.INTEGER)
    private Integer task_id;

    @Column(name = "task_type")
    @Type(value = Types.VARCHAR)
    private String task_type;

    @Column(name = "create_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp create_time;

    @Column(name = "create_user_id")
    @Type(value = Types.INTEGER)
    private Integer create_user_id;

    @Column(name = "approve_user_id")
    @Type(value = Types.INTEGER)
    private Integer approve_user_id;

    private String str_create_time;

    private String create_user_name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTask_id() {
        return task_id;
    }

    public void setTask_id(Integer task_id) {
        this.task_id = task_id;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public Integer getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(Integer create_user_id) {
        this.create_user_id = create_user_id;
    }

    public Integer getApprove_user_id() {
        return approve_user_id;
    }

    public void setApprove_user_id(Integer approve_user_id) {
        this.approve_user_id = approve_user_id;
    }

    public String getStr_create_time() {
        return str_create_time;
    }

    public void setStr_create_time(String str_create_time) {
        this.str_create_time = str_create_time;
    }

    public String getCreate_user_name() {
        return create_user_name;
    }

    public void setCreate_user_name(String create_user_name) {
        this.create_user_name = create_user_name;
    }

}
