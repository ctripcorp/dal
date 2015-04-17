package com.ctrip.platform.dal.dao;

import java.util.Map;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;
import com.ctrip.platform.dal.dao.task.BatchDeleteTask;
import com.ctrip.platform.dal.dao.task.BatchInsertTask;
import com.ctrip.platform.dal.dao.task.BatchUpdateTask;
import com.ctrip.platform.dal.dao.task.BulkTask;
import com.ctrip.platform.dal.dao.task.CombinedInsertTask;
import com.ctrip.platform.dal.dao.task.SingleDeleteTask;
import com.ctrip.platform.dal.dao.task.SingleInsertTask;
import com.ctrip.platform.dal.dao.task.SingleTask;
import com.ctrip.platform.dal.dao.task.SingleUpdateTask;
import com.ctrip.platform.dal.dao.task.DalTaskFactory;

/**
 * This Factory is to unify Ctrip special MS Sql Server CUD case and common my sql case. 
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
public class CtripTaskFactory implements DalTaskFactory {
	@Override
	public void initialize(Map<String, ?> settings) {
		//Do noting for now
	}


	private <T> DatabaseCategory getDbCategory(DalParser<T> parser) {
		DatabaseCategory dbCategory = DalClientFactory.getDalConfigure().getDatabaseSet(parser.getDatabaseName()).getDatabaseCategory();
		if(DatabaseCategory.MySql == dbCategory || DatabaseCategory.SqlServer == dbCategory )
			return dbCategory;
					
		throw new RuntimeException("Such Db category not suported yet");
	}
		
	@Override
	public <T> SingleTask<T> createSingleInsertTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		SingleTask<T> singleTask;
		
		if(DatabaseCategory.MySql == dbCategory)
			singleTask = new SingleInsertTask<T>();
		else
			singleTask = new SingleInsertSpaTask<>();
		
		singleTask.initialize(parser);
		return singleTask;
	}

	@Override
	public <T> SingleTask<T> createSingleDeleteTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		SingleTask<T> singleTask;

		if(DatabaseCategory.MySql == dbCategory)
			singleTask = new SingleDeleteTask<T>();
		else
			singleTask = new SingleDeleteSpaTask<>();

		singleTask.initialize(parser);
		return singleTask;
	}

	@Override
	public <T> SingleTask<T> createSingleUpdateTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		SingleTask<T> singleTask;

		if(DatabaseCategory.MySql == dbCategory)
			singleTask = new SingleUpdateTask<T>();
		else
			singleTask = new SingleUpdateSpaTask<>();

		singleTask.initialize(parser);
		return singleTask;
	}

	@Override
	public <T> BulkTask<Integer, T> createCombinedInsertTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		BulkTask<Integer, T> bulkTask;

		if(DatabaseCategory.MySql == dbCategory)
			bulkTask = new CombinedInsertTask<T>();
		else
			bulkTask = null;

		bulkTask.initialize(parser);
		return bulkTask;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchInsertTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		BulkTask<int[], T> bulkTask;

		if(DatabaseCategory.MySql == dbCategory)
			bulkTask = new BatchInsertTask<T>();
		else
			bulkTask = new BatchInsertSp3Task<T>();

		bulkTask.initialize(parser);
		return bulkTask;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchDeleteTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		BulkTask<int[], T> bulkTask;

		if(DatabaseCategory.MySql == dbCategory)
			bulkTask = new BatchDeleteTask<T>();
		else
			bulkTask = new BatchDeleteSp3Task<T>();

		bulkTask.initialize(parser);
		return bulkTask;
	}

	@Override
	public <T> BulkTask<int[], T> createBatchUpdateTask(DalParser<T> parser) {
		DatabaseCategory dbCategory = getDbCategory(parser);
		BulkTask<int[], T> bulkTask;

		if(DatabaseCategory.MySql == dbCategory)
			bulkTask = new BatchUpdateTask<T>();
		else
			bulkTask = new BatchUpdateSp3Task<T>();

		bulkTask.initialize(parser);
		return bulkTask;
	}
}
