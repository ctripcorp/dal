package com.ctrip.platform.dao;

import java.util.Map;

public interface DalParser<T> extends DalRowMapper<T> {
	String getDatabaseName();
	String getTableName();
	String[] getColumnNames();
	Map<String, ?> getFields(T pojo);
	
	boolean hasIdentityColumn();
	String getIdentityColumnName();
	Number getIdentityValue(T pojo);
	
	Map<String, ?> getPk(T pojo);
}
