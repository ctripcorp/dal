package com.ctrip.platform.dal.dao.task;

import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.detectDistributedTransaction;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.executeByDbShard;
import static com.ctrip.platform.dal.dao.helper.DalShardingHelper.isAlreadySharded;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalDetailResults;
import com.ctrip.platform.dal.dao.DalHints;
import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.client.DalWatcher;

public class TaskExecutor<T> {
	private DalParser<T> parser;
	private final String logicDbName;
	private final String rawTableName;

	public TaskExecutor(DalParser<T> parser) {
		this.parser = parser;
		logicDbName = parser.getDatabaseName();
		rawTableName = parser.getTableName();
	}
	
	public int execute(DalHints hints, T[] daoPojos, SingleTask task) throws SQLException {
		if(isEmpty(daoPojos)) return 0;
		
		return execute(hints, Arrays.asList(daoPojos), task);
	}
	
	// TODO revise return type
	public int execute(DalHints hints, List<T> daoPojos, SingleTask task) throws SQLException {
		if(isEmpty(daoPojos)) return 0;

		List<Map<String, ?>> pojos = getPojosFields(daoPojos);
		detectDistributedTransaction(logicDbName, hints, pojos);
		
		int count = 0;
		hints = hints.clone();// To avoid shard id being polluted by each pojos
		for (Map<String, ?> fields : pojos) {
			DalWatcher.begin();// TODO check if we needed

			try {
				count += task.execute(hints, fields);
			} catch (SQLException e) {
				// TODO do we need log error here?
				if (hints.isStopOnError())
					throw e;
			}
		}
		
		return count;	
	}
	
	public <K> K execute(DalHints hints, T[] daoPojos, BulkTask<K> task, K emptyValue) throws SQLException {
		if(isEmpty(daoPojos)) return emptyValue;
		
		return execute(hints, Arrays.asList(daoPojos), task, emptyValue);
	}
	
	public <K> K execute(DalHints hints, List<T> daoPojos, BulkTask<K> task, K emptyValue) throws SQLException {
		if(isEmpty(daoPojos)) return emptyValue;
		
		hints.setDetailResults(new DalDetailResults<K>());

		if(isAlreadySharded(logicDbName, rawTableName, hints))
			return task.execute(hints, getPojosFields(daoPojos));
		else
			return executeByDbShard(logicDbName, rawTableName, hints, getPojosFields(daoPojos), task);
	}
	
	private boolean isEmpty(List<T> daoPojos) {
		return null == daoPojos || daoPojos.size() == 0;
	}
	
	private boolean isEmpty(T... daoPojos) {
		if(null == daoPojos)
			return true;
		
		return daoPojos.length == 1 && daoPojos[0] == null;
	}
	
	public List<Map<String, ?>> getPojosFields(List<T> daoPojos) {
		List<Map<String, ?>> pojoFields = new LinkedList<Map<String, ?>>();
		if (null == daoPojos || daoPojos.size() < 1)
			return pojoFields;
		
		for (T pojo: daoPojos){
			pojoFields.add(parser.getFields(pojo));
		}
		
		return pojoFields;
	}
}
