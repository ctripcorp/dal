package com.ctrip.platform.dal.dao.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;
import com.ctrip.platform.dal.dao.logging.Logger;
import com.ctrip.platform.dal.dao.strategy.DalShardStrategy;

public class DalTransactionManager {
	public static final boolean SELECTE = true;
	public static final boolean UPDATE = false;

	private DalConfigure config;
	private String logicDbName;
	private DruidDataSourceWrapper connPool;

	private static final ThreadLocal<ConnectionCache> connectionCacheHolder = new ThreadLocal<ConnectionCache>();

	public DalTransactionManager(DalConfigure config, String logicDbName) {
		this.config = config;
		this.logicDbName = logicDbName;
	}
	
	public DalTransactionManager(String logicDbName, DruidDataSourceWrapper connPool) {
		this.connPool = connPool;
		this.logicDbName = logicDbName;
	}
	
	public int startTransaction(DalHints hints) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			Connection conn = getConnection(hints, true);
			conn.setAutoCommit(false);
			connCache = new ConnectionCache(hints.getInt(DalHintEnum.oldIsolationLevel), conn, logicDbName);
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

	public boolean isInTransaction() {
		return connectionCacheHolder.get() != null;
	}
	
	public void rollbackTransaction(int startLevel) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			// Already handled in deeper level
//			throw new SQLException("calling endTransaction with empty ConnectionCache");
			return;
		}

		connCache.rollbackTransaction(startLevel);
	}
	
	public Connection getConnection(DalHints hints) throws SQLException {
		return getConnection(hints, false);
	}
	
	private Connection getConnection(DalHints hints, boolean useMaster) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			return getNewConnection(hints, useMaster);
		} else {
			connCache.validate(logicDbName);
			return connCache.getConnection();
		}
	}

	private Connection getNewConnection(DalHints hints, boolean useMaster)
			throws SQLException {
		Connection conn = null;
		String realDbName = logicDbName;
		try
		{
			boolean isMaster = hints.is(DalHintEnum.masterOnly) || useMaster;
			boolean isSelect = hints.get(DalHintEnum.operation) == DalEventEnum.QUERY;
			
			// The internal test path
			if(config == null) {
				conn = connPool.getConnection(logicDbName, isMaster, isSelect);
			}else {
				conn = getConnectionFromDSLocator(hints, isMaster, isSelect);
			}
			
			conn.setAutoCommit(true);
			applyHints(hints, conn);

			realDbName = conn.getCatalog();
			Logger.logGetConnectionSuccess(realDbName);
		}
		catch(SQLException ex)
		{
			Logger.logGetConnectionFailed(realDbName, ex);
			throw ex;
		}
		return conn;
	}

	private Connection getConnectionFromDSLocator(DalHints hints,
			boolean isMaster, boolean isSelect) throws SQLException {
		Connection conn;
		String realDbName;
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		if(dbSet.isShardingSupported()){
			DalShardStrategy strategy = dbSet.getStrategy();

			// In case the sharding strategy indicate that master shall be used
			isMaster |= strategy.isMaster(config, logicDbName, hints);
			String shard = strategy.locateShard(config, logicDbName, hints);
			dbSet.validate(shard);
			
			realDbName = dbSet.getRandomRealDbName(shard, isMaster, isSelect);
		} else {
			realDbName = dbSet.getRandomRealDbName(isMaster, isSelect);
		}
		
		try {
			conn = DataSourceLocator.newInstance().getDataSource(realDbName).getConnection();
		} catch (Exception e) {
			throw new SQLException(e);
		}
		return conn;
	}
	
	private void applyHints(DalHints hints, Connection conn) throws SQLException {
		Integer level = hints.getInt(DalHintEnum.isolationLevel);
		
		if(level == null || conn.getTransactionIsolation() == level) {
			// Make sure this hints
			hints.set(DalHintEnum.oldIsolationLevel, null);
			return;
		}

		hints.set(DalHintEnum.oldIsolationLevel, conn.getTransactionIsolation());
		conn.setTransactionIsolation(level);
	}

	private static void restoreIsolation(Integer oldLevel, Connection conn) throws SQLException {
		if(oldLevel == null) {
			return;
		}
		
		conn.setTransactionIsolation(oldLevel);
	}
	
	private static void closeConnection(Integer oldLevel, Connection conn) {
		try {
			if(!conn.isClosed()){
				restoreIsolation(oldLevel, conn);
				conn.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	private void closeConnection(DalHints hints, Connection conn) {
		if(conn == null) 
			return;
		
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			closeConnection(hints.getInt(DalHintEnum.oldIsolationLevel), conn);
		} else {
			//do nothing
		}
	}
	
	public void cleanup(DalHints hints, ResultSet rs, Statement statement, Connection conn) {
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
		
		closeConnection(hints, conn);
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
		private String logicDbName;
		private Connection conn;
		private Integer oldLevel;
		private int level = 0;
		private boolean rolledBack;
		
		public ConnectionCache(Integer oldLevel, Connection conn, String logicDbName) throws SQLException{
			this.logicDbName = logicDbName;
			this.conn = conn;
			this.oldLevel = oldLevel;
			conn.setAutoCommit(false);
		}
		
		public void validate(String logicDbName) throws SQLException {
			if(logicDbName == null || logicDbName.length() == 0)
				throw new SQLException("Logic Db Name is empty!");
			
			if(!logicDbName.equals(this.logicDbName))
				throw new SQLException(String.format("DAL do not support distributed transaction. Current DB: %s, DB requested: %s", this.logicDbName, logicDbName));
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
			
			closeConnection(oldLevel, conn);			
			connectionCacheHolder.set(null);
		}
	}
}
