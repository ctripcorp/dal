package com.ctrip.platform.dal.dao;

import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.TaskFactory;

/**
 * This DAO is to simplify Ctrip special MS Sql Server CUD case. 
 * Ctrip use SP3 or SPA to perform CUD on MS Sql Server.
 * The rules:
 * 1. If there are both SP3 and SPA for the table, the batch CUD will use SP3, the non-batch will use SPA.
 *    The reason is because a special setting in Ctrip Sql Server that prevent batch SPA CUD
 * 2. If there is only SP3 for the table, both batch and non-batch will using SP3
 * 3. If there is only SPA for the table, only non-batch CUD supported
 * 4. If there is no SP3 or SPA, the original DalTableDao should be used.
 * 5. For insert SP3 and SPA, the auto incremental Id will be used as output parameter
 * 
 * For sharding support: it is confirmed from DBA that Ctrip has shard by DB case, but no shard by table case.
 * For inout, out parameter: only insert SP3/SPA has inout/out parameter
 * 
 * @author jhhe
 */
public class CtripSpTaskFactory<T> implements TaskFactory<T>{
	private SingleInsertSpaTask<T> singleInsertSpaTask;
	private SingleDeleteSpaTask<T> singleDeleteSpaTask = new SingleDeleteSpaTask<>();
	private SingleUpdateSpaTask<T> singleUpdateSpaTask = new SingleUpdateSpaTask<>();

	private BatchInsertSp3Task<T> batchInsertSp3Task = new BatchInsertSp3Task<>();
	private BatchDeleteSp3Task<T> batchDeleteSp3Task = new BatchDeleteSp3Task<>();
	private BatchUpdateSp3Task<T> batchUpdateSp3Task = new BatchUpdateSp3Task<>();

	public CtripSpTaskFactory() {
		singleInsertSpaTask = new SingleInsertSpaTask<>();
	}
	
	@Override
	public void initialize(DalParser<T> parser) {
		singleInsertSpaTask.initialize(parser);
		singleDeleteSpaTask.initialize(parser);
		singleUpdateSpaTask.initialize(parser);
		
		batchInsertSp3Task.initialize(parser);
		batchDeleteSp3Task.initialize(parser);
		batchUpdateSp3Task.initialize(parser);
	}

	@Override
	public SingleTask<T> createSingleInsertTask() {
		return singleInsertSpaTask;
	}

	@Override
	public SingleTask<T> createSingleDeleteTask() {
		return singleDeleteSpaTask;
	}

	@Override
	public SingleTask<T> createSingleUpdateTask() {
		return singleUpdateSpaTask;
	}

	@Override
	public BulkTask<Integer, T> createCombinedInsertTask() {
		throw new RuntimeException("CombinedInsert is not currently supported for ctrip DAO");
	}

	@Override
	public BulkTask<int[], T> createBatchInsertTask() {
		return batchInsertSp3Task;
	}

	@Override
	public BulkTask<int[], T> createBatchDeleteTask() {
		return batchDeleteSp3Task;
	}

	@Override
	public BulkTask<int[], T> createBatchUpdateTask() {
		return batchUpdateSp3Task;
	}
}
