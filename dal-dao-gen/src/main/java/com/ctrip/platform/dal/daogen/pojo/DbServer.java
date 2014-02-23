package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DbServer {
	
	private int id;
	
	private String driver;
	
	private String server;
	
	private int port;
	
	private String domain;
	
	private String user;
	
	private String password;
	
	private String db_type;

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

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
		data.setServer(rs.getString(3));
		data.setPort(rs.getInt(4));
		data.setDomain(rs.getString(5));
		data.setUser(rs.getString(6));
		data.setPassword(rs.getString(7));
		data.setDb_type(rs.getString(8));
		return data;
	}

}
