package com.ctrip.platform.dal.dao;

import java.util.Map;

public interface DalParser<T> extends DalRowMapper<T> {
	String getDatabaseName();
	String getTableName();
	String[] getColumnNames();
	
	boolean hasIdentityColumn();
	String getIdentityColumnName();
	String[] getPrimaryKeyNames();
	
	Map<String, ?> getFields(T pojo);
	Number getIdentityValue(T pojo);
	Map<String, ?> getPk(T pojo);
}
