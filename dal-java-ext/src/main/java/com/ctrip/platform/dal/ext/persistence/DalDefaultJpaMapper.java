package com.ctrip.platform.dal.ext.persistence;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalRowMapper;

public class DalDefaultJpaMapper {
	
	private static final ConcurrentHashMap<String, DalRowMapper<?>> cache = new ConcurrentHashMap<String, DalRowMapper<?>>();

	@SuppressWarnings("unchecked")
	public static <T> DalRowMapper<T> create(Class<T> clazz, String databaseName) throws SQLException {
		if (!cache.contains(clazz.getName())) {
			synchronized (DalDefaultJpaMapper.class) {
				if (!cache.contains(clazz.getName())) {
					createAndCacheMapper(clazz, databaseName);
				}
			}
		}
		return (DalRowMapper<T>) cache.get(clazz.getName());
	}
	
	private static <T> void createAndCacheMapper(Class<T> clazz, String databaseName) throws SQLException {
		final EntityManager<T> manager = new EntityManager<T>(clazz);
		DalRowMapper<T> mapper = new DalRowMapper<T>() {
			Class<T> clazz = manager.getClazz();
			String[] columnNames = manager.getColumnNames();
			Map<String, Field> fieldsMap = manager.getFieldMap();
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
		};
		cache.put(clazz.getName(), mapper);
	}
}
