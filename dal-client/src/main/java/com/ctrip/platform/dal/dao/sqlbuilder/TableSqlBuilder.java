package com.ctrip.platform.dal.dao.sqlbuilder;

import java.sql.SQLException;

import com.ctrip.platform.dal.common.enums.DatabaseCategory;

public interface TableSqlBuilder extends SqlBuilder {
	TableSqlBuilder from(String tableName) throws SQLException;
	
	TableSqlBuilder setDatabaseCategory(DatabaseCategory dbCategory) throws SQLException;
	
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
