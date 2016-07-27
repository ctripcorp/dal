package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Project {
    private int id;

    private String name;

    private String namespace;

    private int dal_group_id;

    private String dal_config_name;

    private String update_user_no;

    private Timestamp update_time;
    private String str_update_time = "";

    private String text;

    private String icon;

    private boolean children;


    public static Project visitRow(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt(1));
        project.setName(rs.getString(2));
        project.setNamespace(rs.getString(3));
        project.setDal_group_id(rs.getInt(4));
        project.setDal_config_name(rs.getString(5));
        project.setUpdate_user_no(rs.getString(6));
        project.setUpdate_time(rs.getTimestamp(7));
        try {
            Date date = new Date(project.getUpdate_time().getTime());
            project.setStr_update_time(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
        } catch (Throwable e) {
        }
        project.setText(project.getName());
        project.setChildren(false);
        project.setIcon("glyphicon glyphicon-tasks");
        return project;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getDal_group_id() {
        return dal_group_id;
    }

    public void setDal_group_id(int dal_group_id) {
        this.dal_group_id = dal_group_id;
    }

    public String getDal_config_name() {
        return dal_config_name;
    }

    public void setDal_config_name(String dal_config_name) {
        this.dal_config_name = dal_config_name;
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

    public String getStr_update_time() {
        return str_update_time;
    }

    public void setStr_update_time(String str_update_time) {
        this.str_update_time = str_update_time;
    }

}
