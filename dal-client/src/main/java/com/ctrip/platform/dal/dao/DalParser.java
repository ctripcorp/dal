package com.ctrip.platform.dal.dao;

import java.util.Map;

public interface DalParser<T> extends DalRowMapper<T> {
	String getDatabaseName();
	String getTableName();
	
	String[] getColumnNames();

	String[] getPrimaryKeyNames();
	int[] getColumnTypes();

	String[] getSensitiveColumnNames();
	
	/**
	 * Assumption: the auto incremental column is also the primary key. 
	 * @return
	 */
	boolean isAutoIncrement();

	Number getIdentityValue(T pojo);
	
	/**
	 * For building where clause for update/delete operation
	 */
	Map<String, ?> getPrimaryKeys(T pojo);

	/**
	 * For insert/update/delete operation
	 */
	Map<String, ?> getFields(T pojo);
}
