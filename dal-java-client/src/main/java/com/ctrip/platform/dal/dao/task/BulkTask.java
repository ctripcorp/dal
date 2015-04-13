package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

public interface BulkTask<K, T> extends DaoTask<T> {
	K execute(DalHints hints, List<Map<String, ?>> shaffled) throws SQLException;
	K merge(List<K> results);
}
