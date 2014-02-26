package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProject {

	private int id;
	
	private int project_id;
	
	private String userNo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProject_id() {
		return project_id;
	}

	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public static UserProject visitRow(ResultSet rs) throws SQLException {
		UserProject task = new UserProject();
		task.setId(rs.getInt(1));
		task.setProject_id(rs.getInt(2));
		task.setUserNo(rs.getString(3));
		return task;
	}

	
}
