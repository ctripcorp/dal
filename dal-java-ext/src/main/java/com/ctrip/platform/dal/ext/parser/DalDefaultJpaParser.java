package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;
import com.ctrip.platform.dal.ext.persistence.EntityManager;

/**
 * 
 * If Use this parser, the JPA Entity should flow the rules
 * 	1.The entity must contain non-parameters constructor.
 *  2.Each field of the entity must declare the SqlType annotation.
 */
public class DalDefaultJpaParser<T> extends AbstractDalParser<T> {
	
	private static final ConcurrentHashMap<String, DalParser<?>> cache = new ConcurrentHashMap<String, DalParser<?>>();
	private Map<String, Field> fieldsMap;
	private Class<T> clazz;
	private Field identity;
	private boolean autoIncrement;
	
	private DalDefaultJpaParser(Class<T> clazz, boolean autoIncrement,
			String dataBaseName, String tableName,
			String[] columns, String[] primaryKeyColumns, int[] columnTypes,
			Map<String, Field> fieldsMap){
		super(dataBaseName, tableName, columns, primaryKeyColumns, columnTypes);
		this.clazz = clazz;
		this.autoIncrement = autoIncrement;
		this.fieldsMap = fieldsMap;
	}

	@SuppressWarnings("unchecked")
	public static <T> DalParser<T> create(Class<T> clazz, String databaseName) throws SQLException {
		if (!cache.contains(clazz.getName())) {
			synchronized (DalDefaultJpaParser.class) {
				if (!cache.contains(clazz.getName())) {
					createAndCacheParser(clazz, databaseName);
				}
			}
		}
		return (DalParser<T>) cache.get(clazz.getName());
	}

	public static <T> void createAndCacheParser(Class<T> clazz, String databaseName) throws SQLException {
		EntityManager manager = new EntityManager(clazz);
		String tableName = manager.getTableName();
		boolean autoIncrement = manager.isAutoIncrement();
		String[] primaryKeyNames = manager.getPrimaryKeyNames();
		Map<String, Field> fieldsMap = manager.getFieldMap();
		String[] columnNames = manager.getColumnNames();
		int[] columnTypes = manager.getColumnTypes();
		
		DalParser<T> parser = new DalDefaultJpaParser<T>(
				clazz, autoIncrement, databaseName, tableName, 
				columnNames, primaryKeyNames, columnTypes, fieldsMap);
		
		cache.put(clazz.getName(), parser);
	}
	
	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance = this.clazz.newInstance();
			String[] primaryKeyNames = this.getPrimaryKeyNames();
			for (int i = 0; i < primaryKeyNames.length; i++) {
				Field field = this.fieldsMap.get(primaryKeyNames[i]);
				EntityManager.setValue(field, instance, rs.getObject(primaryKeyNames[i]));
			}
			return instance;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	@Override
	public Number getIdentityValue(T pojo) {
		if (pojo.getClass().equals(this.clazz)) {
			try {
				Object val = this.identity.get(pojo);
				if (val instanceof Number)
					return (Number) val;
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String[] primaryKeyNames = this.getPrimaryKeyNames();
		if (primaryKeyNames != null) {
			for (int i = 0; i < primaryKeyNames.length; i++) {
				try {
					Field field = this.fieldsMap.get(primaryKeyNames[i]);
					Object val = EntityManager.getValue(field, pojo);
					map.put(primaryKeyNames[i], val);
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return map;
	}

	@Override
	public Map<String, ?> getFields(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int i = 0; i < this.getColumnNames().length; i++) {
			try {
				Field field = this.fieldsMap.get(this.getColumnNames()[i]);
				if (this.autoIncrement && field.equals(this.identity)) {
					map.put(this.getColumnNames()[i], null);
					continue;
				}
				Object val = EntityManager.getValue(field, pojo);
				map.put(this.getColumnNames()[i], val);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}

}
