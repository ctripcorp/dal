package com.ctrip.platform.dal.dao.client;

import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.logging.DalEventEnum;

public class DalTransactionManager {
	private DalConnectionManager connManager;

	private static final ThreadLocal<ConnectionCache> connectionCacheHolder = new ThreadLocal<ConnectionCache>();

	public DalTransactionManager(DalConnectionManager connManager) {
		this.connManager = connManager;
	}
	
	public int startTransaction(DalHints hints, DalEventEnum operation) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();

		if(connCache == null) {
			connCache = new ConnectionCache( 
					getConnection(hints, true, operation), 
					connManager.getLogicDbName());
			
			connectionCacheHolder.set(connCache);
		}
		return connCache.startTransaction();
	}

	public void endTransaction(int startLevel) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null)
			throw new SQLException("calling endTransaction with empty ConnectionCache");

		connCache.endTransaction(startLevel);
	}

	public static boolean isInTransaction() {
		return connectionCacheHolder.get() != null;
	}
	
	public void rollbackTransaction(int startLevel) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		// Already handled in deeper level
		if(connCache == null)
			return;

		connCache.rollbackTransaction(startLevel);
	}
	
	public ConnectionHolder getConnection(DalHints hints, DalEventEnum operation) throws SQLException {
		return getConnection(hints, false, operation);
	}
	
	public DbMeta getCurrentDbMeta() throws SQLException {
		return connectionCacheHolder.get().getConnection().getMeta();
	}
	
	private ConnectionHolder getConnection(DalHints hints, boolean useMaster, DalEventEnum operation) throws SQLException {
		ConnectionCache connCache = connectionCacheHolder.get();
		
		if(connCache == null) {
			return connManager.getNewConnection(hints, useMaster, operation);
		} else {
			connCache.validate(connManager.getLogicDbName());
			return connCache.getConnection();
		}
	}
	
	public static void clearCache() {
		connectionCacheHolder.set(null);
	}	
}
