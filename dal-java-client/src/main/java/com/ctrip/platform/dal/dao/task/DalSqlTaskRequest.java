package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.common.enums.ParameterDirection;
import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameter;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
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
	private Map<String, List<?>> parametersByShard;
	
	public DalSqlTaskRequest(String logicDbName, String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task, ResultMerger<T> merger) {
		logger = DalClientFactory.getDalLogger();
		this.logicDbName = logicDbName;
		this.sql = sql;
		this.parameters = parameters;
		this.hints = hints;
		this.task = task;
		this.merger = merger;
	}
	
	@Override
	public void validate() throws SQLException {
		if(sql == null)
			throw new DalException(ErrorCode.ValidateSql);
		
		detectDistributedTransaction(shards = getShards());
	}

	@Override
	public boolean isCrossShard() {
		return shards != null && shards.size() > 1;
	}

	@Override
	public Callable<T> createTask() throws SQLException {
		DalHints tmpHints = hints.clone();
		if(shards != null && shards.size() == 1) {
			tmpHints.inShard(shards.iterator().next());
		}

		return new SqlTaskCallable<>(DalClientFactory.getClient(logicDbName), sql, parameters, tmpHints, task);
	}

	@Override
	public Map<String, Callable<T>> createTasks() throws SQLException {
		Map<String, Callable<T>> tasks = new HashMap<>();
		if(parametersByShard == null) {
			// Create by given shards
			for(String shard: shards) {
				tasks.put(shard, new SqlTaskCallable<>(DalClientFactory.getClient(logicDbName), sql, parameters, hints.clone().inShard(shard), task));
			}
		}else{
			// Create by sharded values
			for(Map.Entry<String, ?> shard: parametersByShard.entrySet()) {
				StatementParameters tempParameters = parameters.duplicateWith(shard.getKey(), (List)shard.getValue());
				tasks.put(shard.getKey(), new SqlTaskCallable<>(DalClientFactory.getClient(logicDbName), sql, tempParameters, hints.clone().inShard(shard.getKey()), task));
			}
		}
		return tasks;
	}

	@Override
	public ResultMerger<T> getMerger() {
		return merger;
	}
	
	private Set<String> getShards() throws SQLException {
		Set<String> shards = null;
		if(!DalShardingHelper.isShardingEnabled(logicDbName))
			return null;
		
		if(hints.isAllShards()) {
			DatabaseSet set = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName);
			shards = set.getAllShards();
			logger.warn("Execute on all shards detected: " + sql);
		} else if(hints.isInShards()){
			shards = (Set<String>)hints.get(DalHintEnum.shards);
			logger.warn("Execute on multiple shards detected: " + sql);
		} else if(hints.isShardBy()){
			String shardColName = hints.getShardBy();
			// Check parameters. It can only surpport DB shard at this level
			StatementParameter parameter = parameters.get(shardColName, ParameterDirection.Input);
			if(parameter.getValue() instanceof List) {
				parametersByShard = DalShardingHelper.shuffle(logicDbName, (List)parameter.getValue());
				if(parametersByShard != null)
					return parametersByShard.keySet();
			}
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
