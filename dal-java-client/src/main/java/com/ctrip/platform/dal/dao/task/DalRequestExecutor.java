package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultCallback;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.client.DalWatcher;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * Common reuqest executor that support execute request that is of pojo or 
 * sql in single, all or multiple shards
 * 
 * @author jhhe
 */
public class DalRequestExecutor {
	private static ExecutorService service = null;
	
	static {
		service = new ThreadPoolExecutor(5, 50, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * TODO make the shutdown register
	 */
	public static void shutdown() {
		if (service != null)
			service.shutdown();
	}

	public <T> T execute(final DalHints hints, final DalRequest<T> request) throws SQLException {
		return execute(hints, request, false);
	}
	
	public <T> T execute(final DalHints hints, final DalRequest<T> request, final boolean nullable) throws SQLException {
		// TODO change queryCallback to ResultCallback
		// TODO add performance tracking DalWatcher.begin();

		if (hints.isAsyncExecution()) {
			Future<T> future = service.submit(new Callable<T>() {
				public T call() throws Exception {
					return internalExecute(hints, request, nullable);
				}
			});
			
			if(hints.isAsyncExecution())
				hints.set(DalHintEnum.futureResult, future); 
			return null;
		}
		
		return internalExecute(hints, request, nullable);
	}

	private <T> T internalExecute(DalHints hints, DalRequest<T> request, boolean nullable) throws SQLException {
		request.validate();
		
		T result;

		/**
		 * TODO make sure detect distributed transaction 
		 */
		if(request.isCrossShard())
			result = crossShardExecute(hints, request);
		else
			result = nonCrossShardExecute(hints, request);

		if(result == null && !nullable)
			throw new DalException(ErrorCode.AssertNull);
		
		handleCallback(hints, result);
		
		return result;
	}

	private <T> T nonCrossShardExecute(DalHints hints, DalRequest<T> request) throws SQLException {
		try {
			return request.createTask().call();
		} catch (Exception e) {
			throw DalException.wrap(e);
		}
	}
	
	private <T> T crossShardExecute(DalHints hints, DalRequest<T> request) throws SQLException {
		DalWatcher.crossShardBegin();
		
		T result = hints.is(DalHintEnum.sequentialExecution)?
				seqncialExecute(hints, request):
				parallelExecute(hints, request);
		
		DalWatcher.crossShardEnd();
		return result;
			
	}

	private <T> void handleCallback(final DalHints hints, T result) {
		DalResultCallback qc = (DalResultCallback)hints.get(DalHintEnum.resultCallback);
		if (qc != null)
			qc.onResult(result);
	}

	private <T> T parallelExecute(DalHints hints, DalRequest<T> request) throws SQLException {
		Map<String, Callable<T>> tasks = request.createTasks();
		Map<String, Future<T>> resultFutures = new HashMap<>();
		
		for(final String shard: tasks.keySet())
			resultFutures.put(shard, service.submit(tasks.get(shard)));

		// TODO Handle timeout and execution exception
		ResultMerger<T> merger = request.getMerger();
		for(Map.Entry<String, Future<T>> entry: resultFutures.entrySet()) {
			try {
				merger.addPartial(entry.getKey(), entry.getValue().get());
			} catch (Throwable e) {
				hints.handleError("There is error during parallel execution: ", e);
			}
		}
		
		return merger.merge();
	}

	private <T> T seqncialExecute(DalHints hints, DalRequest<T> request) throws SQLException {
		Map<String, Callable<T>> tasks = request.createTasks();
		ResultMerger<T> merger = request.getMerger();
		for(final String shard: tasks.keySet()) {
			try {
				merger.addPartial(shard, tasks.get(shard).call());
			} catch (Throwable e) {
				hints.handleError("There is error during sequential execution: ", e);
			}
		}
		
		return merger.merge();
	}
}
