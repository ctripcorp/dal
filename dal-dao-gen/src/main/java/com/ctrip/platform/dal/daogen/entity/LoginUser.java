package com.ctrip.platform.dal.daogen.entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginUser {
	
	private int id;
	
	private String userNo;
	
	private String userName;
	
	private String userEmail;
	
	private String role;//组员的权限
	
	private String adduser;//是否可以添加组员
	
	public static LoginUser visitRow(ResultSet rs) throws SQLException {
		LoginUser task = new LoginUser();
		task.setId(rs.getInt(1));
		task.setUserNo(rs.getString(2));
		task.setUserName(rs.getString(3));
		task.setUserEmail(rs.getString(4));
		return task;
	}
	
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getAdduser() {
		return adduser;
	}

	public void setAdduser(String adduser) {
		this.adduser = adduser;
	}

}
