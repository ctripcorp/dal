package com.ctrip.platform.dal.dao.client;

import java.sql.Connection;
import java.sql.SQLException;

import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.DalConfigure;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.configure.SelectionContext;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalConnectionManager {
	private DalConfigure config;
	private String logicDbName;
	private DalLogger logger;
	private DalConnectionLocator locator;

	public DalConnectionManager(String logicDbName, DalConfigure config) {
		this.logicDbName = logicDbName;
		this.config = config;
		this.logger = config.getDalLogger();
		this.locator = config.getLocator();
	}

	public String getLogicDbName() {
		return logicDbName;
	}

	public DalConfigure getConfig() {
		return config;
	}

	public DalLogger getLogger() {
		return logger;
	}

	public DalConnection getNewConnection(DalHints hints, boolean useMaster, DalEventEnum operation)
			throws SQLException {
		DalConnection connHolder = null;
		String realDbName = logicDbName;
		try
		{
			if(DalStatusManager.getDatabaseSetStatus(logicDbName).isMarkdown())
				throw new DalException(ErrorCode.MarkdownLogicDb, logicDbName);

			boolean isMaster = hints.is(DalHintEnum.masterOnly) || useMaster;
			boolean isSelect = operation == DalEventEnum.QUERY;

			connHolder = getConnectionFromDSLocator(hints, isMaster, isSelect);

			connHolder.setAutoCommit(true);
			connHolder.applyHints(hints);

			if(hints.getHA() != null){
				hints.getHA().setDatabaseCategory(connHolder.getMeta().getDatabaseCategory());
			}

			realDbName = connHolder.getDatabaseName();
		}
		catch(SQLException ex)
		{
			logger.getConnectionFailed(realDbName, ex);
			throw ex;
		}
		return connHolder;
	}

	public String evaluateShard(DalHints hints) throws SQLException {
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		String shardId;

		if(!dbSet.isShardingSupported())
			return null;

		DalShardingStrategy strategy = dbSet.getStrategy();

		shardId = hints.getShardId();
		if(shardId == null)
			shardId = strategy.locateDbShard(config, logicDbName, hints);

		// We allow this happen
		if(shardId == null)
			return null;

		dbSet.validate(shardId);

		return shardId;
	}

	private DalConnection getConnectionFromDSLocator(DalHints hints,
													 boolean isMaster, boolean isSelect) throws SQLException {
		Connection conn;
		String allInOneKey;
		DatabaseSet dbSet = config.getDatabaseSet(logicDbName);
		String shardId = null;

		if(dbSet.isShardingSupported()){
			DalShardingStrategy strategy = dbSet.getStrategy();

			// In case the sharding strategy indicate that master shall be used
			isMaster |= strategy.isMaster(config, logicDbName, hints);
			shardId = hints.getShardId();
			if(shardId == null)
				shardId = strategy.locateDbShard(config, logicDbName, hints);
			if(shardId == null)
				throw new DalException(ErrorCode.ShardLocated, logicDbName);
			dbSet.validate(shardId);
		}

		allInOneKey = select(logicDbName, dbSet, hints, shardId, isMaster, isSelect);

		try {
			conn = locator.getConnection(allInOneKey);
			DbMeta meta = DbMeta.createIfAbsent(allInOneKey, dbSet.getDatabaseCategory(), conn);
			return new DalConnection(conn, isMaster, shardId, meta);
		} catch (Throwable e) {
			throw new DalException(ErrorCode.CantGetConnection, e, allInOneKey);
		}
	}

	private String select(String logicDbName, DatabaseSet dbSet, DalHints hints, String shard, boolean isMaster, boolean isSelect) throws DalException {
		SelectionContext context = new SelectionContext(logicDbName, hints, shard, isMaster, isSelect);

		if(shard == null) {
			context.setMasters(dbSet.getMasterDbs());
			context.setSlaves(dbSet.getSlaveDbs());
		}else{
			context.setMasters(dbSet.getMasterDbs(shard));
			context.setSlaves(dbSet.getSlaveDbs(shard));
		}

		return config.getSelector().select(context);
	}

	public <T> T doInConnection(ConnectionAction<T> action, DalHints hints)
			throws SQLException {
		// If HA disabled or not query, we just directly call _doInConnnection

		if(!DalStatusManager.getHaStatus().isEnabled()
				|| action.operation != DalEventEnum.QUERY)
			return _doInConnection(action, hints);

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
			MarkdownManager.detect(action.connHolder, action.start, e);
			action.error(e);
		} finally {
			action.endExecute();
			action.populateDbMeta();
			action.cleanup();
		}

		action.end(result);
		return result;
	}
}