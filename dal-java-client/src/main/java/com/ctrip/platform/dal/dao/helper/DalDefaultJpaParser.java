package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.DalRowMapper;

/**
 * 
 * If Use this parser, the JPA Entity should flow the rules
 * 	1.The entity must contain non-parameters constructor.
 *  2.Each field of the entity must declare the SqlType annotation.
 */
public class DalDefaultJpaParser<T> extends AbstractDalParser<T> {
	
	private Map<String, Field> fieldsMap;
	private Class<T> clazz;
	private Field identity;
	private boolean autoIncrement;
	private DalRowMapper<T> rowMapper;
	private String[] sensitiveColumnNames; 
	
	private DalDefaultJpaParser(Class<T> clazz, boolean autoIncrement,
			String dataBaseName, String tableName, String[] columns, 
			String[] primaryKeyColumns, int[] columnTypes, Map<String, Field> fieldsMap, 
			Field identity, DalRowMapper<T> rowMapper, String[] sensitiveColumnNames) {
		super(dataBaseName, tableName, columns, primaryKeyColumns, columnTypes);
		this.clazz = clazz;
		this.identity = identity;
		this.autoIncrement = autoIncrement;
		this.fieldsMap = fieldsMap;
		this.rowMapper = rowMapper;
		this.sensitiveColumnNames = sensitiveColumnNames;
	}

	public static <T> DalParser<T> create(Class<T> clazz, String databaseName) throws SQLException {
		EntityManager<T> manager = new EntityManager<T>(clazz);
		String tableName = manager.getTableName();
		boolean autoIncrement = manager.isAutoIncrement();
		String[] primaryKeyNames = manager.getPrimaryKeyNames();
		Map<String, Field> fieldsMap = manager.getFieldMap();
		String[] columnNames = manager.getColumnNames();
		int[] columnTypes = manager.getColumnTypes();
		Field[] identities = manager.getIdentity();
		Field identity = identities != null && identities.length == 1 ? identities[0] : null;
		DalRowMapper<T> rowMapper = new DalDefaultJpaMapper<T>(clazz);
		String[] sensitiveColumnNames = manager.getSensitiveColumnNames();
		return new DalDefaultJpaParser<T>(
				clazz, autoIncrement, databaseName, tableName, columnNames, primaryKeyNames,
				columnTypes, fieldsMap, identity, rowMapper, sensitiveColumnNames);
	}

	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		return rowMapper.map(rs, rowNum);
	}

	@Override
	public boolean isAutoIncrement() {
		return this.autoIncrement;
	}

	@Override
	public Number getIdentityValue(T pojo) {
		if (pojo.getClass().equals(this.clazz) && identity != null) {
			try {
				Object val = EntityManager.getValue(identity, pojo);
				if (val instanceof Number)
					return (Number) val;
			} catch (Throwable e) {
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
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}
		}
		return map;
	}

	@Override
	public Map<String, ?> getFields(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		String[] columnNames = this.getColumnNames();
		for (int i = 0; i < columnNames.length; i++) {
			try {
				Field field = this.fieldsMap.get(columnNames[i]);
				Object val = EntityManager.getValue(field, pojo);
				map.put(columnNames[i], val);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}
	
	public String[] getSensitiveColumnNames() {
		return this.sensitiveColumnNames;
	}
}
