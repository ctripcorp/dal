package com.ctrip.platform.dal.dao.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;

public class DalTransactionManager {
	public static final boolean SELECTE = true;
	public static final boolean UPDATE = false;

	private String logicDbName;
	private DruidDataSourceWrapper connPool;

	private static final ThreadLocal<ConnectionCache> connectionCacheHolder = new ThreadLocal<ConnectionCache>();

	public DalTransactionManager(String logicDbName, DruidDataSourceWrapper connPool) {
		this.connPool = connPool;
		this.logicDbName = logicDbName;
	}
	
	public int startTransaction(DalHints hints) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			Connection conn = connPool.getConnection(logicDbName, isMaster(hints), UPDATE);
			conn.setAutoCommit(false);
			connCache = new ConnectionCache(conn);
			connectionCacheHolder.set(connCache);
		}
		return connCache.startTransaction();
	}

	public void endTransaction(int startLevel) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			throw new SQLException("calling endTransaction with empty ConnectionCache");
		}

		connCache.endTransaction(startLevel);
	}

	public void rollbackTransaction(int startLevel) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
//			throw new SQLException("calling endTransaction with empty ConnectionCache");
			return;
		}

		connCache.rollbackTransaction(startLevel);
	}

	public Connection getConnection(DalHints hints, boolean isSelect) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			Connection conn = connPool.getConnection(logicDbName, isMaster(hints), isSelect);
			conn.setAutoCommit(true);
			return conn;
		} else {
			return connCache.getConnection();
		}
	}

	public void closeConnection(Connection conn) {
		if(conn == null) 
			return;
		
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			try {
				conn.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			//do nothing
		}
	}
	
	public void cleanup(Statement statement, Connection conn) {
		cleanup(null, statement, conn);
	}
	
	public void cleanup(ResultSet rs, Statement statement, Connection conn) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		closeConnection(conn);
	}
	
	public SQLException handleException(Throwable e, int startLevel) {
		try {
			rollbackTransaction(startLevel);
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
		return e instanceof SQLException ? (SQLException)e : new SQLException(e);
	}
	
	public SQLException handleException(Throwable e) {
		return e instanceof SQLException ? (SQLException)e : new SQLException(e);
	}
	
	private static final class ConnectionCache {
		private Connection conn;
		private int level = 0;
		private boolean rolledBack;
		
		public ConnectionCache(Connection conn) throws SQLException{
			this.conn = conn;
			conn.setAutoCommit(false);
		}
		
		public Connection getConnection() {
			return conn;
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
			try {
				if(commit)
					conn.commit();
				else
					conn.rollback();
			} catch (Throwable e) {
				e.printStackTrace();
			}

			try {
				conn.setAutoCommit(false);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
			try {
				conn.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
			connectionCacheHolder.set(null);
		}
	}

	private boolean isMaster(DalHints hints) {
		// TODO add more check here
		return null != hints && hints.contains(DalHintEnum.masterOnly);
	}
}
