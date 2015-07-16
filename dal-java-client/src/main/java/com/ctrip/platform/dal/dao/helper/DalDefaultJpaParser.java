package com.ctrip.platform.dal.dao.helper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

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
	
	public DalDefaultJpaParser(Class<T> clazz) throws SQLException {
		EntityManager<T> manager = new EntityManager<T>(clazz);
		this.dataBaseName = manager.getDatabaseName();
		this.tableName = manager.getTableName();
		this.columns = manager.getColumnNames();
		this.primaryKeyColumns = manager.getPrimaryKeyNames();
		this.columnTypes = manager.getColumnTypes();
		this.clazz = clazz;
		this.autoIncrement = manager.isAutoIncrement();
		this.fieldsMap = manager.getFieldMap();
		Field[] identities = manager.getIdentity();
		this.identity = identities != null && identities.length == 1 ? identities[0] : null;
		this.rowMapper = new DalDefaultJpaMapper<T>(clazz);
		this.sensitiveColumnNames = manager.getSensitiveColumnNames();
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
