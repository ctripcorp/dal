package com.ctrip.platform.dal.dao.task;

import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalHints;

public interface SingleTask {
	int execute(DalHints hints, Map<String, ?> daoPojo) throws SQLException;
}
