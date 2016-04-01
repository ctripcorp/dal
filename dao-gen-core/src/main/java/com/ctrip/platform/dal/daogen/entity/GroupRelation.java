package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class GroupRelation {
    private int id;

    private int current_group_id;

    private int child_group_id;
    //子类组的角色，1：当前组的管理员，2：受限用户
    private int child_group_role = 2;

    private int adduser = 2;

    private String update_user_no;

    private Timestamp update_time;

    public static GroupRelation visitRow(ResultSet rs) throws SQLException {
        GroupRelation relation = new GroupRelation();
        relation.setId(rs.getInt("id"));
        relation.setCurrent_group_id(rs.getInt("current_group_id"));
        relation.setChild_group_id(rs.getInt("child_group_id"));
        relation.setChild_group_role(rs.getInt("child_group_role"));
        relation.setAdduser(rs.getInt("adduser"));
        relation.setUpdate_user_no(rs.getString("update_user_no"));
        relation.setUpdate_time(rs.getTimestamp("update_time"));
        return relation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrent_group_id() {
        return current_group_id;
    }

    public void setCurrent_group_id(int current_group_id) {
        this.current_group_id = current_group_id;
    }

    public int getChild_group_id() {
        return child_group_id;
    }

    public void setChild_group_id(int child_group_id) {
        this.child_group_id = child_group_id;
    }

    public int getChild_group_role() {
        return child_group_role;
    }

    public void setChild_group_role(int child_group_role) {
        this.child_group_role = child_group_role;
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

    public int getAdduser() {
        return adduser;
    }

    public void setAdduser(int adduser) {
        this.adduser = adduser;
    }

}
