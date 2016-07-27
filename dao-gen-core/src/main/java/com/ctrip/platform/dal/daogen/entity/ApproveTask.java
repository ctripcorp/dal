package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ApproveTask {
    private int id;
    private int task_id;
    private String task_type;
    private Timestamp create_time;
    private int create_user_id;
    private int approve_user_id;

    private String str_create_time;
    private String create_user_name;

    public static ApproveTask visitRow(ResultSet rs) throws SQLException {
        ApproveTask task = new ApproveTask();
        task.setId(rs.getInt("id"));
        task.setTask_id(rs.getInt("task_id"));
        task.setTask_type(rs.getString("task_type"));
        task.setCreate_time(rs.getTimestamp("create_time"));
        task.setCreate_user_id(rs.getInt("create_user_id"));
        task.setApprove_user_id(rs.getInt("approve_user_id"));
        try {
            Date date = new Date(task.getCreate_time().getTime());
            task.setStr_create_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        } catch (Throwable e) {
        }
        return task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTask_id() {
        return task_id;
    }

    public void setTask_id(int task_id) {
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

    public int getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(int create_user_id) {
        this.create_user_id = create_user_id;
    }

    public int getApprove_user_id() {
        return approve_user_id;
    }

    public void setApprove_user_id(int approve_user_id) {
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
