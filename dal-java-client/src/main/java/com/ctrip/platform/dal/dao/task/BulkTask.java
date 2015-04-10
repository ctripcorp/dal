package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

public interface BulkTask<T> {
	T execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException;
	T merge(List<T> results);
}
