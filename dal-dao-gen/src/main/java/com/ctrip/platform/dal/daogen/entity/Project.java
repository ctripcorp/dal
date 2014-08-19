package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Project {
	
	private int id;
	
	private String name;
	
	private String namespace;
	
	private int dal_group_id; 
	
	private String dal_config_name;
	
	private String text;
	
	private String icon;
	
	private boolean children;

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

	public static Project visitRow(ResultSet rs) throws SQLException {
		Project project = new Project();
        project.setId(rs.getInt(1));
        project.setName(rs.getString(2));
        project.setNamespace(rs.getString(3));
        project.setDal_group_id(rs.getInt(4));
        project.setDal_config_name(rs.getString(5));
        project.setText(project.getName());
        project.setChildren(false);
        project.setIcon("fa fa-tasks");
        return project;
	}

}
