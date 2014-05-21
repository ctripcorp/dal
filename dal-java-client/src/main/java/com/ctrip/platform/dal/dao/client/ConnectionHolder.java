package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;

public class ConnectionHolder {
	private Connection conn;
	private DbMeta meta;
	public ConnectionHolder(Connection conn, DbMeta meta) {
		this.conn = conn;
		this.meta = meta;
	}

	public Connection getConn() {
		return conn;
	}

	public DbMeta getMeta() {
		return meta;
	}
}
