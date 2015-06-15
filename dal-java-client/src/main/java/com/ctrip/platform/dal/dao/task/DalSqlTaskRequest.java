package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalSqlTaskRequest<T> implements DalRequest<T>{
	private DalLogger logger;
	private String logicDbName;
	private String sql;
	private StatementParameters parameters;
	private DalHints hints;
	private SqlTask<T> task;
	private ResultMerger<T> merger;
	private Set<String> shards;
	
	public DalSqlTaskRequest(String logicDbName, String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task, ResultMerger<T> merger) {
		logger = DalClientFactory.getDalLogger();
		this.logicDbName = logicDbName;
		this.sql = sql;
		this.parameters = parameters;
		this.hints = hints;
		this.task = task;
		this.merger = merger;
		this.shards = getShards();
	}
	
	@Override
	public void validate() throws SQLException {
		if(sql == null)
			throw new DalException(ErrorCode.ValidateSql);
		
		detectDistributedTransaction(shards);
	}

	@Override
	public boolean isCrossShard() {
		return shards != null && shards.size() > 1;
	}

	@Override
	public Callable<T> createTask() throws SQLException {
		return new SqlTaskCallable<>(DalClientFactory.getClient(logicDbName), sql, parameters, hints.clone(), task);
	}

	@Override
	public Map<String, Callable<T>> createTasks() throws SQLException {
		Map<String, Callable<T>> tasks = new HashMap<>();
		for(String shard: shards)
			tasks.put(shard, createTask());

		return tasks;
	}

	@Override
	public ResultMerger<T> getMerger() {
		return merger;
	}
	
	private Set<String> getShards() {
		Set<String> shards;
		
		if(hints.is(DalHintEnum.allShards)) {
			DatabaseSet set = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName);
			shards = set.getAllShards();
			logger.warn("Execute on all shards detected: " + sql);
		} else {
			shards = (Set<String>)hints.get(DalHintEnum.shards);
			logger.warn("Execute on multiple shards detected: " + sql);
		}
		
		return shards;
	}
	
	private static class SqlTaskCallable<T> implements Callable<T> {
		private DalClient client;
		private String sql;
		private StatementParameters parameters;
		private DalHints hints;
		private SqlTask<T> task;

		public SqlTaskCallable(DalClient client, String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task){
			this.client = client;
			this.sql = sql;
			this.parameters = parameters;
			this.hints = hints;
			this.task = task;			
		}

		@Override
		public T call() throws Exception {
			return task.execute(client, sql, parameters, hints);
		}
	}
}
