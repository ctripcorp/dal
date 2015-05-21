package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.dao.DalClient;
import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.QueryCallback;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.StatementParameters;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.configure.DatabaseSet;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * Support execute given sql and parameter in single, all or multiple shards
 * 
 * @author jhhe
 *
 */
public class SqlExecutor {
	private DalLogger logger;
	private String logicDbName;
	private DalClient client;
	
	public SqlExecutor(String logicDbName) {
		this.logicDbName = logicDbName;
		client = DalClientFactory.getClient(logicDbName);
		logger = DalClientFactory.getDalLogger();
	}
	
	private static ExecutorService service = null;
	
	static {
		service = new ThreadPoolExecutor(5, 50, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public static void shutdownAsyncTaskExecutor() {
		if (service != null)
			service.shutdown();
	}

	public <T> T execute(final String sql, final StatementParameters parameters, final DalHints hints, final SqlTask<T> task) throws SQLException {
		// TODO change queryCallback to ResultCallback
		if (hints.isAsyncExecution() || hints.is(DalHintEnum.queryCallback)) {
			Future<T> future = service.submit(new Callable<T>() {
				public T call() throws Exception {
					return internalExecute(sql, parameters, hints, task);
				}
			});
			
			if(hints.isAsyncExecution())
				hints.set(DalHintEnum.futureResult, future); 
			return null;
		}
		
		//There is no callback
		return internalExecute(sql, parameters, hints, task);
	}

	private <T> T internalExecute(String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task) throws SQLException {
		
		// Check if it is in (distributed) transaction
		Set<String> shards = getShards(sql, hints);
		
		T result;
		// Not the cross shard query, just query normally
		if(shards == null) 
			result = task.execute(client, sql, parameters, hints);
		else
			result = crossShardExecute(sql, parameters, hints, task, shards);

		boolean nullable = true;
		
		if(result == null && !nullable)
			throw new DalException(ErrorCode.AssertNull);
		
		handleCallback(hints, result);
		
		return result;
	}

	private <T> T crossShardExecute(String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task, Set<String> shards) throws SQLException {
		return hints.is(DalHintEnum.sequentialExecution)?
			seqncialExecute(sql, parameters, hints, task, shards):
			parallelExecute(sql, parameters, hints, task, shards);
	}

	private <T> void handleCallback(final DalHints hints, T result) {
		QueryCallback qc = (QueryCallback)hints.get(DalHintEnum.queryCallback);
		if (qc != null)
			qc.onResult(result);
	}

	private <T> T parallelExecute(final String sql, final StatementParameters parameters, final DalHints hints, final SqlTask<T> task, Set<String> shards) throws SQLException {
		Map<String, Future<T>> resultFutures = new HashMap<>();
		for(final String shard: shards)
			resultFutures.put(shard, service.submit(new  Callable<T>() {
				public T call() throws Exception {
					return task.execute(client, sql, parameters, hints);
				}
			}));

		ResultMerger<T> merger = task.getMerger();
		// TODO Handle timeout and execution exception
		for(Map.Entry<String, Future<T>> entry: resultFutures.entrySet()) {
			try {
				merger.addPartial(entry.getKey(), entry.getValue().get());
			} catch (Throwable e) {
				if(hints.isStopOnError())
					throw DalException.wrap(ErrorCode.Unknown, e);
				
				DalClientFactory.getDalLogger().warn("There is error during parallel execution: " + e.getMessage());
			}
		}
		
		return merger.merge();
	}

	private <T> T seqncialExecute(String sql, StatementParameters parameters, DalHints hints, SqlTask<T> task, Set<String> shards) throws SQLException {
		ResultMerger<T> merger = task.getMerger();
		for(final String shard: shards) {
			try {
				merger.addPartial(shard, task.execute(client, sql, parameters, hints));
			} catch (Throwable e) {
				if(hints.isStopOnError())
					throw DalException.wrap(ErrorCode.Unknown, e);
				
				DalClientFactory.getDalLogger().warn("There is error during sequential execution: " + e.getMessage());
			}
		}
		
		return merger.merge();
	}
	
	private Set<String> getShards(String sql, DalHints hints) {
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
}
