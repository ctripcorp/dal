package com.ctrip.platform.dal.dao.client;

import com.ctrip.platform.dal.cluster.Cluster;
import com.ctrip.platform.dal.cluster.base.HostSpec;
import com.ctrip.platform.dal.cluster.shard.DatabaseShard;
import com.ctrip.platform.dal.cluster.util.StringUtils;
import com.ctrip.platform.dal.dao.DalEventEnum;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.configure.*;
import com.ctrip.platform.dal.dao.datasource.DataSourceIdentity;
import com.ctrip.platform.dal.dao.markdown.MarkdownManager;
import com.ctrip.platform.dal.dao.status.DalStatusManager;
import com.ctrip.platform.dal.dao.strategy.DalShardingStrategy;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.DalRuntimeException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

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

		DataBase selectedDataBase = select(logicDbName, dbSet, hints, shardId, isMaster, isSelect);

		try {
			DbMeta meta;
			if (selectedDataBase instanceof ClusterDataBase) {
				ClusterDataBase clusterDataBase = (ClusterDataBase) selectedDataBase;
				conn = locator.getConnection(clusterDataBase);
				meta = DbMeta.createIfAbsent(clusterDataBase, dbSet.getDatabaseCategory(), conn);
				if (shardId == null)
					shardId = String.valueOf(clusterDataBase.getDatabase().getShardIndex());
			}
			else if (selectedDataBase instanceof ProviderDataBase) {
				DataSourceIdentity id = selectedDataBase.getDataSourceIdentity();
				conn = locator.getConnection(id);
				meta = DbMeta.createIfAbsent(id, dbSet.getDatabaseCategory(), conn);
			}
			else {
				String allInOneKey = selectedDataBase.getConnectionString();
				conn = locator.getConnection(allInOneKey);
				meta = DbMeta.createIfAbsent(allInOneKey, dbSet.getDatabaseCategory(), conn);
			}
			return new DalConnection(conn, selectedDataBase.isMaster(), shardId, meta);
		} catch (Throwable e) {
			throw new DalException(ErrorCode.CantGetConnection, e, selectedDataBase.getConnectionString());
		}
	}

	private DataBase select(String logicDbName, DatabaseSet dbSet, DalHints hints, String shard, boolean isMaster, boolean isSelect) throws DalException {
		if (dbSet instanceof ClusterDatabaseSet && !((ClusterDatabaseSet) dbSet).getCluster().getRouteStrategyConfig().multiMaster()) {
			return clusterSelect(dbSet, hints, shard, isMaster, isSelect);
		}

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

	protected DataBase clusterSelect(DatabaseSet dbSet, DalHints hints, String shard, boolean isMaster, boolean isSelect) {
		Cluster cluster = ((ClusterDatabaseSet) dbSet).getCluster();
		DatabaseShard databaseShard;
		if (StringUtils.isEmpty(shard)) {
			Set<Integer> shards = cluster.getAllDbShards();
			if (shards.size() == 0)
				throw new DalRuntimeException("no shards found for this cluster");
			if (shards.size() > 1)
				throw new DalRuntimeException("multiple shards detected for non sharding cluster");
			databaseShard = cluster.getDatabaseShard(shards.iterator().next());
		}
		else
			databaseShard = cluster.getDatabaseShard(Integer.valueOf(shard));

		if (isMaster || !isSelect) {
			return new ClusterDataBase(databaseShard.getMasters().iterator().next());
		}

		HostSpec hostSpec = databaseShard.getRouteStrategy().pickRead(config.getSelector().parseDalHints(hints));
		return new ClusterDataBase(databaseShard.parseFromHostSpec(hostSpec));
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