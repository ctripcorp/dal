package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.dao.StatementParameters;

public interface SqlBuilder extends Cloneable {
	/**
	 * @return Build the final sql, the table should contains table shrd id if necessary
	 */
	String build();
	
	/**
	 * @return raw table name without shard id if any
	 */
	String getTableName();
	
	/**
	 * To build with table shard id and separator if present
	 * @param shardStr
	 * @param separator
	 * @return
	 */
	String build(String shardStr);
	
	/**
	 * @return the parameters used
	 */
	StatementParameters buildParameters();
}
