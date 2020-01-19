package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalHintEnum;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalResultCallback;
import com.ctrip.platform.dal.dao.ResultMerger;
import com.ctrip.platform.dal.dao.client.DalLogger;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.client.LogEntry;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

/**
 * Common request executor that support execute request that is of pojo or
 * sql in single, all or multiple shards
 * 
 * @author jhhe
 */
public class DalRequestExecutor {
	private static AtomicReference<ExecutorService> serviceRef = new AtomicReference<>();
	
	public static final String MAX_POOL_SIZE = "maxPoolSize";
	public static final String KEEP_ALIVE_TIME = "keepAliveTime";
	
	// To be consist with default connection max active size
	public static final int DEFAULT_MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

	public static final int DEFAULT_KEEP_ALIVE_TIME = 10;
	
	private DalLogger logger = DalClientFactory.getDalLogger();
	
	private final static String NA = "N/A";
	        
	public static void init(String maxPoolSizeStr, String keepAliveTimeStr){
		if(serviceRef.get() != null)
			return;
		
		synchronized (DalRequestExecutor.class) {
			if(serviceRef.get() != null)
				return;
			
			int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
			if(maxPoolSizeStr != null)
				maxPoolSize = Integer.parseInt(maxPoolSizeStr);
			
			int keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
            if(keepAliveTimeStr != null)
                keepAliveTime = Integer.parseInt(keepAliveTimeStr);
			
            ThreadPoolExecutor executer = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadFactory() {
                AtomicInteger atomic = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "DalRequestExecutor-Worker-" + this.atomic.getAndIncrement());
                }
            });
            executer.allowCoreThreadTimeOut(true);
            
            serviceRef.set(executer);
		}
	} 
	
	public static void shutdown() {
		if (serviceRef.get() == null)
			return;
		
		synchronized (DalRequestExecutor.class) {
			if (serviceRef.get() == null)
				return;
			
			serviceRef.get().shutdown();
			serviceRef.set(null);
		}
	}

	public <T> T execute(final DalHints hints, final DalRequest<T> request) throws SQLException {
		return execute(hints, request, false);
	}
	
	public <T> T execute(final DalHints hints, final DalRequest<T> request, final boolean nullable) throws SQLException {
		if (hints.isAsyncExecution()) {
			Future<T> future = serviceRef.get().submit(new Callable<T>() {
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
		T result = null;
		Throwable error = null;
		
		LogContext logContext = logger.start(request);
		long startTime = System.currentTimeMillis();

		try {
//			request.validate();
			request.validateAndPrepare();
			
			if(request.isCrossShard())
				result = crossShardExecute(logContext, hints, request);
			else
				result = nonCrossShardExecute(logContext, hints, request);

			if(result == null && !nullable)
				throw new DalException(ErrorCode.AssertNull);

			request.endExecution();
		} catch (Throwable e) {
			error = e;
		}

		logContext.setDaoExecuteTime(System.currentTimeMillis() - startTime);
		logger.end(logContext, error);
		
		handleCallback(hints, result, error);
		if(error != null)
			throw DalException.wrap(error);
		
		return result;
	}

	private <T> T nonCrossShardExecute(LogContext logContext, DalHints hints, DalRequest<T> request) throws Exception {
        logContext.setSingleTask(true);
		TaskCallable<T> task = request.createTask();
	    Callable<T> taskWrapper = new RequestTaskWrapper<T>(NA, task, logContext);
		T result = taskWrapper.call();

		logContext.setStatementExecuteTime(task.getDalTaskContext().getStatementExecuteTime());
		logContext.setEntries(toList(task.getDalTaskContext().getLogEntry()));
		return result;
	}
	
	private <T> T crossShardExecute(LogContext logContext, DalHints hints, DalRequest<T> request) throws Exception {
        Map<String, TaskCallable<T>> tasks = request.createTasks();
        logContext.setShards(tasks.keySet());

        boolean isSequentialExecution = hints.is(DalHintEnum.sequentialExecution);
        logContext.setSeqencialExecution(isSequentialExecution);
        
        ResultMerger<T> merger = request.getMerger();
        
	    logger.startCrossShardTasks(logContext, isSequentialExecution);
		
		T result = null;
		Throwable error = null;

		try {
            result = isSequentialExecution?
            		seqncialExecute(hints, tasks, merger, logContext):
            		parallelExecute(hints, tasks, merger, logContext);

        } catch (Throwable e) {
            error = e;
        }
		
		logger.endCrossShards(logContext, error);

		if(error != null)
            throw DalException.wrap(error);
		
		return result;

	}

	private <T> void handleCallback(final DalHints hints, T result, Throwable error) {
		DalResultCallback qc = (DalResultCallback)hints.get(DalHintEnum.resultCallback);
		if (qc == null)
			return;
		
		if(error == null)
			qc.onResult(result);
		else
			qc.onError(error);
	}

	private <T> T parallelExecute(DalHints hints, Map<String, TaskCallable<T>> tasks, ResultMerger<T> merger, LogContext logContext) throws SQLException {
		Map<String, Future<T>> resultFutures = new HashMap<>();

		List<LogEntry> logEntries = new ArrayList<>();
		long maxStatementExecuteTime = 0;
		for(final String shard: tasks.keySet())
			resultFutures.put(shard, serviceRef.get().submit(new RequestTaskWrapper<T>(shard, tasks.get(shard), logContext)));

		for(Map.Entry<String, Future<T>> entry: resultFutures.entrySet()) {
			try {
				merger.addPartial(entry.getKey(), entry.getValue().get());
			} catch (Throwable e) {
				hints.handleError("There is error during parallel execution: ", e);
			}
			TaskCallable task = tasks.get(entry.getKey());
			maxStatementExecuteTime = Math.max(maxStatementExecuteTime, task.getDalTaskContext().getStatementExecuteTime());
			addLogEntry(logEntries, task.getDalTaskContext().getLogEntry());
		}

		logContext.setStatementExecuteTime(maxStatementExecuteTime);
		logContext.setEntries(logEntries);
		return merger.merge();
	}

	private <T> T seqncialExecute(DalHints hints, Map<String, TaskCallable<T>> tasks, ResultMerger<T> merger, LogContext logContext) throws SQLException {
		long totalStatementExecuteTime = 0;
		List<LogEntry> logEntries = new ArrayList<>();
		for(final String shard: tasks.keySet()) {
			try {
				merger.addPartial(shard, new RequestTaskWrapper<T>(shard, tasks.get(shard), logContext).call());
			} catch (Throwable e) {
				hints.handleError("There is error during sequential execution: ", e);
			}
			TaskCallable task = tasks.get(shard);
			totalStatementExecuteTime += task.getDalTaskContext().getStatementExecuteTime();
			addLogEntry(logEntries, task.getDalTaskContext().getLogEntry());
		}

		logContext.setStatementExecuteTime(totalStatementExecuteTime);
		logContext.setEntries(logEntries);
		return merger.merge();
	}

	private void addLogEntry(List<LogEntry> logEntries, LogEntry logEntry) {
		if (logEntry != null) {
			logEntries.add(logEntry);
		}
	}
	
	public static int getPoolSize() {
	    ThreadPoolExecutor executer = (ThreadPoolExecutor)serviceRef.get();
	    if (serviceRef.get() == null)
            return 0;
	    
	    return executer.getPoolSize();
	}

	private List<LogEntry> toList(LogEntry logEntry) {
	    List<LogEntry> logEntries = new ArrayList<>();
	    if (logEntry != null) {
	        logEntries.add(logEntry);
        }
        return logEntries;
    }
}