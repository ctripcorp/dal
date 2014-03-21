package com.ctrip.platform.dal.dao.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.freeway.gen.v2.LogLevel;
import com.ctrip.platform.dal.common.db.DruidDataSourceWrapper;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;
import com.ctrip.platform.dal.dao.logging.Logger;

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
			// Already handled in deeper level
//			throw new SQLException("calling endTransaction with empty ConnectionCache");
			return;
		}

		connCache.rollbackTransaction(startLevel);
	}
	
	public Connection getConnection(DalHints hints) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		
		//SimpleShardHintStrategy test
		/*hints.set(DalHintEnum.shard, "0");
		hints.set(DalHintEnum.masterOnly);*/
		
		if(connCache == null) {
			Connection conn = null;
			String realDbName = logicDbName;
			try
			{
				if(config != null) {
					Set<String> shards = config.getDatabaseSet(logicDbName).getStrategy().locateShards(config, logicDbName, hints);
					// For now, we only access one shard
					String shard = shards.toArray(new String[1])[0];
					conn = getConnection(logicDbName, shard, isMaster(hints), hints.get(DalHintEnum.operation) == DalEventEnum.QUERY);
				}else {
					conn = connPool.getConnection(logicDbName, isMaster(hints), hints.get(DalHintEnum.operation) == DalEventEnum.QUERY);
				}
				conn.setAutoCommit(true);
				
				realDbName = conn.getCatalog();
				hints.set(DalHintEnum.databaseName, realDbName);
				Logger.log("Get connection", DalEventEnum.CONNECTION_SUCCESS, LogLevel.INFO, 
						String.format("Connection %s database successfully", realDbName));
			}
			catch(SQLException ex)
			{
				String logMsg = "Connection " + realDbName + " database failed." +
						System.lineSeparator() + System.lineSeparator() +
						"********** Exception Info **********" + System.lineSeparator() +
						ex.getMessage();
				Logger.log("Get connection", DalEventEnum.CONNECTION_FAILED, LogLevel.ERROR, logMsg);
				
				throw ex;
			}
			return conn;
		} else {
			return connCache.getConnection();
		}
	}
	
	private Connection getConnection(String logicDbName, String shard, boolean isMaster, boolean isSelect) throws SQLException {
		String realDbName = config.getDatabaseSet(logicDbName).getRandomRealDbName(shard, isMaster, isSelect);
		try {
			return DataSourceLocator.newInstance().getDataSource(realDbName).getConnection();
		} catch (Exception e) {
			throw new SQLException(e);
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
		return null != hints && hints.is(DalHintEnum.masterOnly);
	}
}
