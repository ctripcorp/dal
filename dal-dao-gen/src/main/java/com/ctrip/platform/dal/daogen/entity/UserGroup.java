package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserGroup {

	private int id;
	private int user_id;
	private int group_id;
	private int permision = 1;
	
	public static UserGroup visitRow(ResultSet rs) throws SQLException {
		UserGroup ug = new UserGroup();
		ug.setId(rs.getInt("id"));
		ug.setUser_id(rs.getInt("user_id"));
		ug.setGroup_id(rs.getInt("group_id"));
		ug.setPermision(rs.getInt("permision"));
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

	public int getPermision() {
		return permision;
	}

	public void setPermision(int permision) {
		this.permision = permision;
	}
	
	
}
