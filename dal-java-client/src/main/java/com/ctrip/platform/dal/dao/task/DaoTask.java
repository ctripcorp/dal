package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;

public interface DaoTask<T> {
	void initialize(DalParser<T> parser);
}
