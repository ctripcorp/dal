package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;

import com.ctrip.platform.dal.dao.DalHints;

public interface TaskExecutor<T> {
	public int execute(DalHints hints, T daoPojo, SingleTask<T> task) throws SQLException;
	
	public int[] execute(DalHints hints, List<T> daoPojos, SingleTask<T> task) throws SQLException;
	
	public <K> K execute(DalHints hints, List<T> daoPojos, BulkTask<K, T> task, K emptyValue) throws SQLException;
}
