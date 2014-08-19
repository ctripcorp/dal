package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUser {
	
	private int id;
	
	private String userNo;
	
	private String userName;
	
	private String userEmail;
	
	private int groupId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public static LoginUser visitRow(ResultSet rs) throws SQLException {
		LoginUser task = new LoginUser();
		task.setId(rs.getInt(1));
		task.setUserNo(rs.getString(2));
		task.setUserName(rs.getString(3));
		task.setUserEmail(rs.getString(4));
		task.setGroupId(rs.getInt(5));
		return task;
	}

}
