package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionCache  {
	private String logicDbName;
	private ConnectionHolder connHolder;
	private int level = 0;
	private boolean rolledBack;
	
	public ConnectionCache(ConnectionHolder connHolder, String logicDbName) throws SQLException{
		this.logicDbName = logicDbName;
		this.connHolder = connHolder;
		connHolder.getConn().setAutoCommit(false);
	}
	
	public void validate(String logicDbName) throws SQLException {
		if(logicDbName == null || logicDbName.length() == 0)
			throw new SQLException("Logic Db Name is empty!");
		
		if(!logicDbName.equals(this.logicDbName))
			throw new SQLException(String.format("DAL do not support distributed transaction. Current DB: %s, DB requested: %s", this.logicDbName, logicDbName));
	}
	
	public ConnectionHolder getConnection() {
		return connHolder;
	}
	
	public int startTransaction() throws SQLException {
		return level++;
	}
	
	public void endTransaction(int startLevel) throws SQLException {
		if(startLevel != (level - 1)) {
			rollbackTransaction(startLevel);
			throw new SQLException(String.format("Transaction level mismatch. Expected: %d Actual: %d", level, startLevel));
		}
		
		if(--level == 0)
			cleanup(true);
	}
	
	public void rollbackTransaction(int startLevel) throws SQLException {
		if(rolledBack)
			return;

		rolledBack = true;
		// Even the rollback fails, we still set the flag to true;
		cleanup(false);
	}
	
	private void cleanup(boolean commit) {
		Connection conn = connHolder.getConn();
		try {
			if(commit)
				conn.commit();
			else
				conn.rollback();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			conn.setAutoCommit(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		connHolder.closeConnection();
		DalTransactionManager.clearCache();
	}
}
