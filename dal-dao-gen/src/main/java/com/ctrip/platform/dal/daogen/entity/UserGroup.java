package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserGroup {

	private int id;
	private int user_id;
	private int group_id;
	private int role = 1;
	private int adduser = 1;
	
	public static UserGroup visitRow(ResultSet rs) throws SQLException {
		UserGroup ug = new UserGroup();
		ug.setId(rs.getInt("id"));
		ug.setUser_id(rs.getInt("user_id"));
		ug.setGroup_id(rs.getInt("group_id"));
		ug.setRole(rs.getInt("role"));
		ug.setAdduser(rs.getInt("adduser"));
		return ug;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getGroup_id() {
		return group_id;
	}
	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public int getAdduser() {
		return adduser;
	}

	public void setAdduser(int adduser) {
		this.adduser = adduser;
	}

	
}
