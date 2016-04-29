package com.ctrip.platform.dal.dao.sqlbuilder;

public interface TableSqlBuilder extends SqlBuilder {
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
}
