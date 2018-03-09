package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.buildShardStr;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.locateTableShardId;

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
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.helper.DalShardingHelper;
import com.ctrip.platform.dal.dao.sqlbuilder.SqlBuilder;
import com.ctrip.platform.dal.dao.sqlbuilder.TableSqlBuilder;

public class DalSqlTaskRequest<T> implements DalRequest<T>{
    private String caller;
	private DalLogger logger;
	private String logicDbName;
	private SqlBuilder builder;
	private StatementParameters parameters;
	private DalHints hints;
	private SqlTask<T> task;
	private ResultMerger<T> merger;
	private Set<String> shards;
	private Map<String, List<?>> parametersByShard;
	
	public DalSqlTaskRequest(String logicDbName, SqlBuilder builder, DalHints hints, SqlTask<T> task, ResultMerger<T> merger)
			 throws SQLException {
		logger = DalClientFactory.getDalLogger();
		this.logicDbName = logicDbName;
		this.builder = builder;
		this.parameters = builder.buildParameters();
		this.hints = hints;
		this.task = task;
		this.merger = merger;
		shards = getShards();
		this.caller = LogContext.getRequestCaller();
	}
	
    @Override
    public String getCaller() {
        return caller;
    }

    @Override
    public boolean isAsynExecution() {
        return hints.isAsyncExecution();
    }

    @Override
	public void validate() throws SQLException {
		detectDistributedTransaction(shards);
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

		return create(parameters, tmpHints);
	}

	@Override
	public Map<String, Callable<T>> createTasks() throws SQLException {
		Map<String, Callable<T>> tasks = new HashMap<>();
		
		if(parametersByShard == null) {
			// Create by given shards
			for(String shard: shards) {
				tasks.put(shard, create(parameters.duplicate(), hints.clone().inShard(shard)));
			}
		}else{
			// Create by sharded values
			for(Map.Entry<String, ?> shard: parametersByShard.entrySet()) {
				StatementParameters tempParameters = parameters.duplicateWith(hints.getShardBy(), (List)shard.getValue());
				tasks.put(shard.getKey(), create(tempParameters, hints.clone().inShard(shard.getKey())));
			}
		}
		
		return tasks;
	}
	
	private Callable<T> create(StatementParameters parameters, DalHints hints) throws SQLException {
		if(builder instanceof TableSqlBuilder && isTableShardingEnabled(logicDbName, ((TableSqlBuilder)builder).getTableName())){
		    TableSqlBuilder tableBuilder = (TableSqlBuilder)builder;
			String tableShardStr = buildShardStr(logicDbName, locateTableShardId(logicDbName, tableBuilder.getTableName(), hints, parameters, null));
			return new SqlTaskCallable<>(DalClientFactory.getClient(logicDbName), tableBuilder.build(tableShardStr), parameters, hints, task);
		}

		return new SqlTaskCallable<>(DalClientFactory.getClient(logicDbName), builder.build(), parameters, hints, task);
	}

	@Override
	public ResultMerger<T> getMerger() {
		return merger;
	}
	
    @Override
    public void endExecution() {
        
    }
    
	private Set<String> getShards() throws SQLException {
		Set<String> shards = null;
		if(!DalShardingHelper.isShardingEnabled(logicDbName))
			return null;
		
		if(hints.isAllShards()) {
			shards = DalClientFactory.getDalConfigure().getDatabaseSet(logicDbName).getAllShards();
		} else if(hints.isInShards()){
			shards = (Set<String>)hints.get(DalHintEnum.shards);
		} else if(hints.isShardBy()){
			// The new code gen will set hints shardBy to indicate this is a potential cross shard operation
			// Check parameters. It can only surpport DB shard at this level
			StatementParameter parameter = parameters.get(hints.getShardBy(), ParameterDirection.Input);
			parametersByShard = DalShardingHelper.shuffle(logicDbName, (List)parameter.getValue());
			shards = parametersByShard.keySet();
		}
		
		if(shards != null && shards.size() > 1)
			logger.warn("Execute on multiple shards detected: " + builder.build());
		
		return shards;
	}
	
	private static class SqlTaskCallable<T> implements Callable<T> {
		private DalClient client;
		private String sql;
		private StatementParameters parameters;
		private DalHints hints;
		private SqlTask<T> task;

		public SqlTaskCallable(DalClient client, String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task)
				throws SQLException {
			this.client = client;
			this.sql = sql;
			this.hints = hints;
			this.task = task;
			this.parameters = parameters;
			
			compile();
		}
		
		private void compile() throws SQLException {
			// If there is no in clause, just return
			if(!parameters.containsInParameter())
				return;
			
			sql = SQLCompiler.compile(sql, parameters.getAllInParameters());
			parameters.compile();
		}

		@Override
		public T call() throws Exception {
			return task.execute(client, sql, parameters, hints);
		}
	}
}
