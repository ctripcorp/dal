package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

public interface SingleTask<T> extends DaoTask<T> {
	int execute(DalHints hints, Map<String, ?> daoPojo, T rawPojo, DalTaskContext taskContext) throws SQLException;
	DalTaskContext createTaskContext() throws SQLException;
}
