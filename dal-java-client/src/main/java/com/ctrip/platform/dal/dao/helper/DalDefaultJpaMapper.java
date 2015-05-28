package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalDefaultJpaMapper<T> implements DalRowMapper<T> {
	
	private Class<T> clazz = null;
	private String[] columnNames = null;
	private Map<String, Field> fieldsMap = null;
	
	private DalDefaultJpaMapper(Class<T> clazz) throws SQLException {
		this.clazz = clazz;
		EntityManager<T> manager = new EntityManager<T>(clazz);
		this.columnNames = manager.getColumnNames();
		this.fieldsMap = manager.getFieldMap();
	}
	
	public static <T> DalRowMapper<T> create(Class<T> clazz) throws SQLException {
		return new DalDefaultJpaMapper<T>(clazz);
	}

	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance = this.clazz.newInstance();
			for (int i = 0; i < columnNames.length; i++) {
				Field field = fieldsMap.get(columnNames[i]);
				EntityManager.setValue(field, instance, rs.getObject(columnNames[i]));
			}
			return instance;
		} catch (Throwable e) {
			throw new SQLException(e);
		}
	}
}
