package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.dao.StatementParameters;

public interface SqlBuilder {
	/**
	 * @return Build the final sql. This is used to support DB shard
	 */
	String build();
	
	/**
	 * @return the parameters used
	 */
	StatementParameters buildParameters();
}
