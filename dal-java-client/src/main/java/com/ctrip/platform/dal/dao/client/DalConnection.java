package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.sql.logging.Logger;

public class DalConnection {
	private Integer oldIsolationLevel;
	private Integer newIsolationLevel;
	private Connection conn;
	private DbMeta meta;
	
	public DalConnection(Connection conn, DbMeta meta) throws SQLException {
		this.oldIsolationLevel = conn.getTransactionIsolation();
		this.conn = conn;
		this.meta = meta;
	}

	public Connection getConn() {
		return conn;
	}

	public DbMeta getMeta() {
		return meta;
	}
	
	public String getDatabaseName() throws SQLException {
		return meta.getDatabaseName();
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		conn.setAutoCommit(autoCommit);
	}
	
	public void applyHints(DalHints hints) throws SQLException {
		Integer level = hints.getInt(DalHintEnum.isolationLevel);
		
		if(level == null || oldIsolationLevel.equals(level))
			return;
		
		newIsolationLevel = level;
		conn.setTransactionIsolation(level);
	}
	
	public void close() {
		try {
			if(conn == null || conn.isClosed())
				return;
		} catch (Throwable e) {
			Logger.error("Restore connection isolation level failed!", e);
		}

		try {
			if(newIsolationLevel != null)
				conn.setTransactionIsolation(oldIsolationLevel);
		} catch (Throwable e) {
			Logger.error("Restore connection isolation level failed!", e);
		}

		try {
			conn.close();
		} catch (Throwable e) {
			Logger.error("Close connection failed!", e);
		}
		conn = null;
	}
}
