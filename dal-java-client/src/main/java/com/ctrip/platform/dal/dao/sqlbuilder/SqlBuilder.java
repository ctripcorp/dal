package com.ctrip.platform.dal.dao.sqlbuilder;

import com.ctrip.platform.dal.dao.StatementParameters;

public interface SqlBuilder extends Cloneable {
	/**
	 * Build the final sql, the table should contains table shrd id if necessary
	 * @return
	 */
	String build();
	
	String getTableName();
	
	/**
	 * To build with table shard id and separator if present
	 * @param shardStr
	 * @param separator
	 * @return
	 */
	String buildWith(String shardStr);
	
	/**
	 * @return the parameters used
	 */
	StatementParameters buildParameters();
}
