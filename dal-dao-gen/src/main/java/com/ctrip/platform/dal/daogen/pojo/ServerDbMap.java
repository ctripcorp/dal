package com.ctrip.platform.dal.daogen.pojo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerDbMap {
	
	private int id;
	
	private int server_id;
	
	private String db_name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getServer_id() {
		return server_id;
	}

	public void setServer_id(int server_id) {
		this.server_id = server_id;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}
	
	public static ServerDbMap visitRow(ResultSet rs) throws SQLException {
		ServerDbMap map = new ServerDbMap();
		map.setId(rs.getInt(1));
		map.setServer_id(rs.getInt(2));
		map.setDb_name(rs.getString(3));
		return map;
	}

}
