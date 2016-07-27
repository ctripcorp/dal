package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

public interface BulkTask<K, T> extends DaoTask<T> {
	K getEmptyValue();
	
	K execute(DalHints hints, Map<Integer, Map<String, ?>> shaffled) throws SQLException;
	
	//Merger factory, always return a new merger instance
	BulkTaskResultMerger<K> createMerger();
}
