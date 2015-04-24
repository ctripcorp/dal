package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isTableShardingEnabled;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.shuffle;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.shuffleByTable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ctrip.platform.dal.dao.DalClientFactory;
import com.ctrip.platform.dal.dao.DalDetailResults;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.client.DalWatcher;

public class DefaultTaskExecutor<T> implements TaskExecutor<T> {
	
	private DalParser<T> parser;
	private final String logicDbName;
	private final String rawTableName;
	
	private static ExecutorService service = null;
	
	static {
		service = new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public DefaultTaskExecutor(DalParser<T> parser) {
		this.parser = parser;
		logicDbName = parser.getDatabaseName();
		rawTableName = parser.getTableName();
	}
	
	public static void shutdownAsyncTaskExecutor() {
		if (service != null)
			service.shutdown();
	}
	
	public int execute(DalHints hints, T daoPojo, SingleTask<T> task) throws SQLException {
		if(daoPojo == null) throw new NullPointerException("The given pojo is null.");
		
		validate(task);
		
		List<T> daoPojos = new ArrayList<>(1);
		daoPojos.add(daoPojo);
		return execute(hints, daoPojos, task)[0];
	}
	
	public int[] execute(final DalHints hints, final List<T> daoPojos, final SingleTask<T> task) throws SQLException {
		if(isEmpty(daoPojos)) return new int[0];
		
		validate(task);

		final List<Map<String, ?>> pojos = getPojosFields(daoPojos);
		detectDistributedTransaction(logicDbName, hints, pojos);
		
		if (hints.isAsyncExecution()) {
			doInAsyncExecutor(hints, new Callable<int[]>() {
				@Override
				public int[] call() throws Exception {
					return internalExecute(hints, daoPojos, pojos, task);
				}
			});
			return new int[0];
		} else {
			return internalExecute(hints, daoPojos, pojos, task);
		}
	}
	
	private int[] internalExecute(DalHints hints, List<T> daoPojos, List<Map<String, ?>> pojos, SingleTask<T> task) throws SQLException {
		int[] counts = new int[daoPojos.size()];
		DalHints localHints = hints.clone();// To avoid shard id being polluted by each pojos
		for (int i = 0; i < pojos.size(); i++) {
			DalWatcher.begin();// TODO check if we needed
			try {
				counts[i] = task.execute(localHints, pojos.get(i));
			} catch (SQLException e) {
				DalClientFactory.getDalLogger().error("Error when execute insert pojo", e);
				if (localHints.isStopOnError())
					throw e;
			}
		}
		return counts;	
	}
	
	public <K> K execute(final DalHints hints, final List<T> daoPojos, final BulkTask<K, T> task, final K emptyValue) throws SQLException {
		if(isEmpty(daoPojos)) return emptyValue;
		
		validate(task);
		
		if (hints.isAsyncExecution()) {
			doInAsyncExecutor(hints, new Callable<K>() {
				@Override
				public K call() throws Exception {
					return internalExecute(hints, daoPojos, task, emptyValue);
				}
			});
			return null;
		} else {
			return internalExecute(hints, daoPojos, task, emptyValue);
		}
	}
	
	private void doInAsyncExecutor(DalHints hints, Callable<?> callable) throws SQLException {
		Future<?> result = service.submit(callable);
		DalAsyncCallback callback = hints.getDalAsyncCallback();
		if (callback != null)
			callback.setResult(result);
	}
	
	private <K> K internalExecute(DalHints hints, List<T> daoPojos, BulkTask<K, T> task, K emptyValue) throws SQLException {
		hints.setDetailResults(new DalDetailResults<K>());

		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return task.execute(hints, getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, getPojosFields(daoPojos), task);
	}
	
	private <K> K executeByDbShard(String logicDbName, String rawTableName, DalHints hints, List<Map<String, ?>>  daoPojos, BulkTask<K, T> task) throws SQLException {
		DalWatcher.crossShardBegin();
		K result;
		
		if(isShardingEnabled(logicDbName)) {
			List<K> results = new LinkedList<>();
			Map<String, List<Map<String, ?>>> shuffled = shuffle(logicDbName, hints.getShardId(), daoPojos);
			for(String shard: shuffled.keySet()) {
				hints.inShard(shard);
				K tmpResult = executeByTableShard(logicDbName, rawTableName, hints, shuffled.get(shard), task);
				results.add(tmpResult);
			}
			result = task.merge(results);
		} else {
			result = executeByTableShard(logicDbName, rawTableName, hints, daoPojos, task);
		}
		
		DalWatcher.crossShardEnd();
		return result; 
	}
	
	private <K> K executeByTableShard(String logicDbName, String tabelName, DalHints hints, List<Map<String, ?>> daoPojos, BulkTask<K, T> task) throws SQLException {
		if(isTableShardingEnabled(logicDbName, tabelName)) {
			DalHints tmpHints = hints.clone();
			Map<String, List<Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, hints.getTableShardId(), daoPojos);
			
			List<K> results = new ArrayList<>(pojosInTable.size());
			for(String curTableShardId: pojosInTable.keySet()) {
				tmpHints.inTableShard(curTableShardId);
				K result = task.execute(tmpHints, pojosInTable.get(curTableShardId));
				results.add(result);
			}
			return task.merge(results);
		}else{
			return task.execute(hints, daoPojos);
		}
	}
	
	private boolean isEmpty(List<T> daoPojos) {
		if(null == daoPojos)
			throw new NullPointerException("The given pojos are null.");
		
		return daoPojos.size() == 0;
	}
	
	private void validate(DaoTask<T> task) {
		if(task == null)
			throw new NullPointerException("The given dao task is null. Means the calling DAO method is not supported. Please contact your DAL team.");
	}
	
	private List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}
}
