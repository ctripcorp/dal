package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.KeyHolder.*;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.client.LogContext;
import com.ctrip.platform.dal.dao.helper.DalRequestContext;
import com.ctrip.platform.dal.dao.helper.RequestContext;
import com.ctrip.platform.dal.exceptions.DalException;
import com.ctrip.platform.dal.exceptions.ErrorCode;

public class DalBulkTaskRequest<K, T> implements DalRequest<K>{
    private String caller;
	private String logicDbName;
	private String rawTableName;
	private DalHints hints;
	private List<T> rawPojos;
	private List<Map<String, ?>> daoPojos;
	private BulkTask<K, T> task;
	private DalBulkTaskContext<T> taskContext;
	private BulkTaskResultMerger<K> dbShardMerger;
	Map<String, Map<Integer, Map<String, ?>>> shuffled;
	
	public DalBulkTaskRequest(String logicDbName, String rawTableName, DalHints hints, List<T> rawPojos, BulkTask<K, T> task) {
		this.logicDbName = logicDbName;
		this.rawTableName = rawTableName;
		this.hints = hints != null ? hints.clone() : new DalHints();
		this.rawPojos = rawPojos;
		this.task = task;
		this.caller = LogContext.getRequestCaller();
		prepareRequestContext();
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
	public void validateAndPrepare() throws SQLException {
		if(null == rawPojos)
			throw new DalException(ErrorCode.ValidatePojoList);

		if(task == null)
			throw new DalException(ErrorCode.ValidateTask);

		dbShardMerger = task.createMerger();
		daoPojos = task.getPojosFields(rawPojos);

		if (task instanceof InsertTaskAdapter) {
			((InsertTaskAdapter) task).processIdentityField(hints, daoPojos);
		}

		detectBulkTaskDistributedTransaction(logicDbName, hints, daoPojos);
		taskContext = task.createTaskContext(hints, daoPojos, rawPojos);
	}

	private void prepareRequestContext() {
		hints.setRequestContext(null);
		if (task instanceof TaskAdapter) {
			RequestContext ctx = new DalRequestContext().setLogicTableName(((TaskAdapter) task).rawTableName);
			hints.setRequestContext(ctx);
		} else if (hints.getSpecifiedTableName() != null) {
			RequestContext ctx = new DalRequestContext().setLogicTableName(hints.getSpecifiedTableName());
			hints.setRequestContext(ctx);
		}
	}

	@Override
	public boolean isCrossShard() throws SQLException {		
		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return false;
		
		if(isShardingEnabled(logicDbName)) {
			shuffled = shuffle(logicDbName, hints.getShardId(), daoPojos, hints);
			// Only in one or no shard
			return shuffled.size() <= 1 ? false : true;
		}
		
		// Shard at table level or no shard at all 
		return false;
	}

	@Override
	public TaskCallable<K> createTask() throws SQLException {
		hints = hints.clone();
		handleKeyHolder(false);

		// If only one shard is shuffled
		if(shuffled != null) {
			if(shuffled.size() == 0)
				return new BulkTaskCallable<>(logicDbName, rawTableName, hints, new HashMap<Integer, Map<String, ?>>(), task, (DalBulkTaskContext<T>) taskContext.fork());

			String shard = shuffled.keySet().iterator().next();
			return new BulkTaskCallable<>(logicDbName, rawTableName, hints.inShard(shard), shuffled.get(shard), task, (DalBulkTaskContext<T>) taskContext.fork());
		}
	
		// Convert to index map
		Map<Integer, Map<String, ?>> daoPojosMap = new HashMap<>();
		for(int i = 0; i < daoPojos.size(); i++)
			daoPojosMap.put(i, daoPojos.get(i));

		return new BulkTaskCallable<>(logicDbName, rawTableName, hints, daoPojosMap, task, (DalBulkTaskContext<T>) taskContext.fork());
	}

	@Override
	public Map<String, TaskCallable<K>> createTasks() throws SQLException {
		Map<String, TaskCallable<K>> tasks = new HashMap<>();
		
		// I know this is not so elegant.
		handleKeyHolder(true);
		
		for(String shard: shuffled.keySet()) {
			Map<Integer, Map<String, ?>> pojosInShard = shuffled.get(shard);
			
			dbShardMerger.recordPartial(shard, pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]));
			
			tasks.put(shard, new BulkTaskCallable<>(
					logicDbName, rawTableName, hints.clone().inShard(shard), shuffled.get(shard), task, (DalBulkTaskContext<T>) taskContext.fork()));
		}

		return tasks; 
	}
	
	private void handleKeyHolder(boolean requireMerge) {
		if(hints.getKeyHolder() == null)
			return;
		
		hints.getKeyHolder().requireMerge();
	}

	@Override
	public BulkTaskResultMerger<K> getMerger() {
		return dbShardMerger;
	}

	@Override
    public void endExecution() throws SQLException {
        setGeneratedKeyBack(task, hints, rawPojos);
	}

	private static class BulkTaskCallable<K, T> implements TaskCallable<K> {
		private String logicDbName;
		private String rawTableName;
		private DalHints hints;
		private Map<Integer, Map<String, ?>> shaffled;
		private BulkTask<K, T> task;
		private DalBulkTaskContext<T> taskContext;

		public BulkTaskCallable(String logicDbName, String rawTableName, DalHints hints, Map<Integer, Map<String, ?>> shaffled, BulkTask<K, T> task, DalBulkTaskContext<T> taskContext){
			this.logicDbName = logicDbName;
			this.rawTableName = rawTableName;
			this.hints = hints;
			this.shaffled = shaffled;
			this.task = task;
			this.taskContext = taskContext;
		}

		@Override
		public K call() throws Exception {
			if(shaffled.isEmpty())
			    return task.getEmptyValue();
			
			if(isTableShardingEnabled(logicDbName, rawTableName)) {
				return executeByTableShards();
			}else{
				return execute(hints, shaffled, taskContext);
			}
		}

		private K execute(DalHints hints, Map<Integer, Map<String, ?>> pojosInShard, DalBulkTaskContext<T> taskContext) throws SQLException {
		    K partial = null;
		    Throwable error = null;
            Integer[] indexList = pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]);
		    DalHints localHints = prepareLocalHints(task, hints);

            try {
                partial = task.execute(localHints, pojosInShard, (DalBulkTaskContext<T>) taskContext);
            } catch (Throwable e) {
                error = e;
            }

            mergePartial(task, hints.getKeyHolder(), indexList, localHints.getKeyHolder(), error);

            // Upper level may handle continue on error
            if(error != null)
                throw DalException.wrap(error);

		    return partial;
		}

		private K executeByTableShards() throws SQLException {
			BulkTaskResultMerger<K> merger = task.createMerger();
			
			Map<String, Map<Integer, Map<String, ?>>> pojosInTable = shuffleByTable(logicDbName, rawTableName, hints.getTableShardId(), shaffled);
			
			if(pojosInTable.size() > 1 && hints.getKeyHolder() != null) {
				hints.getKeyHolder().requireMerge();
			}
				
			DalHints localHints;
			for(String curTableShardId: pojosInTable.keySet()) {
				Map<Integer, Map<String, ?>> pojosInShard = pojosInTable.get(curTableShardId);

				Integer[] indexList = pojosInShard.keySet().toArray(new Integer[pojosInShard.size()]);

                localHints = prepareLocalHints(task, hints).inTableShard(curTableShardId);

				merger.recordPartial(curTableShardId, indexList);

				Throwable error = null;
                try {
                    K partial = task.execute(localHints, pojosInShard, (DalBulkTaskContext<T>)taskContext);
                    merger.addPartial(curTableShardId, partial);
                } catch (Throwable e) {
                    error = e;
                }

                mergePartial(task, hints.getKeyHolder(), indexList, localHints.getKeyHolder(), error);
                hints.handleError("Error when execute table shard operation", error);
			}
			return merger.merge();
		}

		@Override
		public DalTaskContext getDalTaskContext() {
			return this.taskContext;
		}
	}
}
