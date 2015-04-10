package com.ctrip.platform.dal.dao.task;

import com.ctrip.platform.dal.dao.DalParser;

public class AbstractInsertTask<T> extends TaskAdapter<T> {

	public AbstractInsertTask(DalParser<T> parser) {
		super(parser);
	}

}
