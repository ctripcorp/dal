package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

public interface BulkTask<K, T> extends DaoTask<T> {
	K getEmptyValue();
	
	BulkTaskContext<T> createTaskContext(DalHints hints, List<Map<String, ?>> daoPojos, List<T> rawPojos) throws SQLException;
	
	K execute(DalHints hints, Map<Integer, Map<String, ?>> shaffled, BulkTaskContext<T> taskContext) throws SQLException;
	
	//Merger factory, always return a new merger instance
	BulkTaskResultMerger<K> createMerger();
}
