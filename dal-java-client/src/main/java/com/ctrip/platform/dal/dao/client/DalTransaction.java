package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.logging.Logger;

public class DalTransaction  {
	private String logicDbName;
	private DalConnection connHolder;
	private int level = 0;
	private boolean rolledBack = false;
	private boolean completed = false;
	
	public DalTransaction(DalConnection connHolder, String logicDbName) throws SQLException{
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
	
	public DalConnection getConnection() {
		return connHolder;
	}
	
	public int getLevel() {
		return level;
	}
	
	public boolean isRolledBack() {
		return rolledBack;
	}

	public int startTransaction() throws SQLException {
		if(rolledBack || completed)
			throw new SQLException("The current transaction is already rolled back or completed");
		
		return level++;
	}
	
	public void endTransaction(int startLevel) throws SQLException {
		if(rolledBack || completed)
			throw new SQLException("The current transaction is already rolled back or completed");

		if(startLevel != (level - 1)) {
			rollbackTransaction();
			throw new SQLException(String.format("Transaction level mismatch. Expected: %d Actual: %d", level, startLevel));
		}
		
		if(--level == 0)
			cleanup(true);
	}
	
	public void rollbackTransaction() throws SQLException {
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
			Logger.error("Can not commit or rollback on current connection", e);
		}

		try {
			conn.setAutoCommit(true);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		connHolder.close();
		DalTransactionManager.clearCurrentTransaction();
	}
}
