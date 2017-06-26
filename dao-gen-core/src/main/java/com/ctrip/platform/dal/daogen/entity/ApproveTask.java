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
    private Integer taskId;

    @Column(name = "task_type")
    @Type(value = Types.VARCHAR)
    private String taskType;

    @Column(name = "create_time")
    @Type(value = Types.TIMESTAMP)
    private Timestamp createTime;

    @Column(name = "create_user_id")
    @Type(value = Types.INTEGER)
    private Integer createUserId;

    @Column(name = "approve_user_id")
    @Type(value = Types.INTEGER)
    private Integer approveUserId;

    private String str_create_time;

    private String create_user_name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getApproveUserId() {
        return approveUserId;
    }

    public void setApproveUserId(Integer approveUserId) {
        this.approveUserId = approveUserId;
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
