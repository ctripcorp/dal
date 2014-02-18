package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbServer {
	
	private int id;
	
	private String driver;
	
	private String url;
	
	private String user;
	
	private String password;
	
	private String db_type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDb_type() {
		return db_type;
	}

	public void setDb_type(String db_type) {
		this.db_type = db_type;
	}
	
	public static DbServer visitRow(ResultSet rs) throws SQLException {
		DbServer data = new DbServer();
		data.setId(rs.getInt(1));
		data.setDriver(rs.getString(2));
		data.setUrl(rs.getString(3));
		data.setUser(rs.getString(4));
		data.setPassword(rs.getString(5));
		data.setDb_type(rs.getString(6));
		return data;
	}

}
