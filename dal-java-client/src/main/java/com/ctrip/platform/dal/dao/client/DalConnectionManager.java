package com.ctrip.platform.dal.dao.client;

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

public class DalConnectionManager {
	private DalConfigure config;
	private String logicDbName;
	private DruidDataSourceWrapper connPool;

	public DalConnectionManager(String logicDbName, DalConfigure config) {
		this.logicDbName = logicDbName;
		this.config = config;
	}
	
	public DalConnectionManager(String logicDbName, DruidDataSourceWrapper connPool) {
		this.logicDbName = logicDbName;
		this.connPool = connPool;
	}
	
	public String getLogicDbName() {
		return logicDbName;
	}

	public ConnectionHolder getNewConnection(DalHints hints, boolean useMaster, DalEventEnum operation)
			throws SQLException {
		ConnectionHolder connHolder = null;
		String realDbName = logicDbName;
		try
		{
			boolean isMaster = hints.is(DalHintEnum.masterOnly) || useMaster;
			boolean isSelect = operation == DalEventEnum.QUERY;
			
			// The internal test path
			if(config == null) {
				Connection conn = null;
				conn = connPool.getConnection(logicDbName, isMaster, isSelect);
				connHolder = new ConnectionHolder(conn, DbMeta.getDbMeta(conn.getCatalog(), conn));
			}else {
				connHolder = getConnectionFromDSLocator(hints, isMaster, isSelect);
			}
			
			Connection conn = connHolder.getConn();
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
		return connHolder;
	}

	private ConnectionHolder getConnectionFromDSLocator(DalHints hints,
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
			DbMeta meta = DbMeta.getDbMeta(realDbName, conn);
			return new ConnectionHolder(conn, meta);
		} catch (Throwable e) {
			throw new SQLException("Can not get connection from DB " + realDbName, e);
		}
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

	private void closeConnection(DalHints hints, Connection conn) {
		if(conn == null) 
			return;

		//do nothing for connection in transaction
		if(DalTransactionManager.isInTransaction())
			return;
		
		closeConnection(hints.getInt(DalHintEnum.oldIsolationLevel), conn);
	}	

	public static void closeConnection(Integer oldLevel, Connection conn) {
		try {
			if(!conn.isClosed()){
				restoreIsolation(oldLevel, conn);
				conn.close();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void restoreIsolation(Integer oldLevel, Connection conn) throws SQLException {
		if(oldLevel == null) {
			return;
		}
		
		conn.setTransactionIsolation(oldLevel);
	}
}
