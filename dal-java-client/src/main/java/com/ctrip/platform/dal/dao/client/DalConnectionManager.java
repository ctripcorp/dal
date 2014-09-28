package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;

import com.ctrip.datasource.locator.DataSourceLocator;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configbeans.ConfigBeanFactory;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.sql.exceptions.DalException;
import com.ctrip.platform.dal.sql.exceptions.ErrorCode;
import com.ctrip.platform.dal.sql.logging.DalEventEnum;
import com.ctrip.platform.dal.sql.logging.DalLogger;

public class DalConnectionManager {
	private DalConfigure config;
	private String logicDbName;

	public DalConnectionManager(String logicDbName, DalConfigure config) {
		this.logicDbName = logicDbName;
		this.config = config;
	}
	
	public String getLogicDbName() {
		return logicDbName;
	}

	public DalConnection getNewConnection(DalHints hints, boolean useMaster, DalEventEnum operation)
			throws SQLException {
		DalConnection connHolder = null;
		String realDbName = logicDbName;
		try
		{
			boolean isMaster = hints.is(DalHintEnum.masterOnly) || useMaster;
			boolean isSelect = operation == DalEventEnum.QUERY;
			
			connHolder = getConnectionFromDSLocator(hints, isMaster, isSelect);

			connHolder.setAutoCommit(true);
			connHolder.applyHints(hints);
			
			if(hints.getHA() != null){
				hints.getHA().setProductName(connHolder.getDatabaseProductName());
			}

			realDbName = connHolder.getDatabaseName();
		}
		catch(SQLException ex)
		{
			DalLogger.logGetConnectionFailed(realDbName, ex);
			throw ex;
		}
		return connHolder;
	}

	private DalConnection getConnectionFromDSLocator(DalHints hints,
			boolean isMaster, boolean isSelect) throws SQLException {
		Connection conn;
		String allInOneKey;
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		if(dbSet.isShardingSupported()){
			DalShardingStrategy strategy = dbSet.getStrategy();

			// In case the sharding strategy indicate that master shall be used
			isMaster |= strategy.isMaster(config, logicDbName, hints);
			String shard = hints.getShardId();
			if(shard == null)
				shard = strategy.locateDbShard(config, logicDbName, hints);
			if(shard == null)
				throw new DalException(ErrorCode.ShardLocated, logicDbName);
			dbSet.validate(shard);
			
			allInOneKey = dbSet.getRandomRealDbName(hints.getHA(), shard, isMaster, isSelect);
		} else {
			allInOneKey = dbSet.getRandomRealDbName(hints.getHA(), isMaster, isSelect);
		}
		
		if(allInOneKey == null && hints.getHA().isOver()){
			throw new DalException(ErrorCode.NoMoreConnectionToFailOver);
		}
		
		if(MarkdownManager.isMarkdown(allInOneKey))
			throw new DalException(ErrorCode.MarkdownConnection, allInOneKey);
		try {	
			conn = DataSourceLocator.newInstance().getDataSource(allInOneKey).getConnection();
			DbMeta meta = DbMeta.getDbMeta(allInOneKey,isMaster,isSelect, conn);
			return new DalConnection(conn, meta);
		} catch (Throwable e) {
			throw new DalException(ErrorCode.CantGetConnection, e, allInOneKey);
		}
	}
	
	public <T> T doInConnection(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		// If HA disabled or not query, we just directly call _doInConnnection

		if(!ConfigBeanFactory.getHAConfigBean().isEnable() 
				|| action.operation != DalEventEnum.QUERY)
			return _doInConnection(action, hints);;

		DalHA highAvalible = new DalHA();
		hints.setHA(highAvalible);
		do{
			try {
				return _doInConnection(action, hints);			
			} catch (SQLException e) {
				highAvalible.update(e);	
			}
		}while(highAvalible.needTryAgain());
		
		throw highAvalible.getException();
	}
	
	private <T> T _doInConnection(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		action.initLogEntry(logicDbName, hints);
		action.start();
		
		Throwable ex = null;
		T result = null;
		
		try {
			result = action.execute();
		} catch (Throwable e) {
			MarkdownManager.collectException(action.connHolder, e);
			ex = e;
		} finally {
			action.populateDbMeta();
			action.cleanup();		
		}
		
		action.end(result, ex);
		return result;
	}
}
