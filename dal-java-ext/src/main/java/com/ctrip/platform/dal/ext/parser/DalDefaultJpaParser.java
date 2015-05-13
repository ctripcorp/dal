package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.ctrip.platform.dal.dao.DalParser;
import com.ctrip.platform.dal.dao.helper.AbstractDalParser;

/**
 * The T type must contain non-parameters constructor
 * 
 * @author wcyuan
 * @param <T>
 *            The JPA Entity
 */
public class DalDefaultJpaParser<T> extends AbstractDalParser<T> {
	private static final ConcurrentHashMap<String, DalParser<?>> cache = new ConcurrentHashMap<String, DalParser<?>>();
	private Map<String, Field> fieldsMap;
	private Class<T> clazz;
	private Field identity;
	private boolean autoIncrement;
	private Loader loader;
	
	private DalDefaultJpaParser(Class<T> clazz, Loader loader, boolean autoIncrement,
			String dataBaseName, String tableName,
			String[] columns, String[] primaryKeyColumns, int[] columnTypes,
			Map<String, Field> fieldsMap){
		super(dataBaseName, tableName, columns, primaryKeyColumns, columnTypes);
		this.clazz = clazz;
		this.loader = loader;
		this.autoIncrement = autoIncrement;
		this.fieldsMap = fieldsMap;
	}

	public static <T> DalParser<T> create(Class<T> clazz, Loader loader, String databaseName) throws SQLException {
		if (!cache.contains(clazz.getName())) {
			
			String tableName = "";
			String[] columnNames = null;
			int[] columnTypes = null;
			boolean autoIncrement = false;
			
			Map<String, Field> fieldsMap = new HashMap<String, Field>();
			Entity entityAnnot = clazz.getAnnotation(Entity.class);
			if (null == entityAnnot)
				throw new SQLException("The parse class: + " + clazz.getName() +  "is not a JPA Entity, pls check.");
			tableName = entityAnnot.name();
			Field[] fields = clazz.getDeclaredFields();
			if (null == fields || fields.length == 0)
				throw new SQLException("The parse class has not any fields.");
			columnNames = new String[fields.length];
			columnTypes = new int[fields.length];
			List<String> primaryKeyNames = new ArrayList<String>();
			
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				field.setAccessible(true);
				
				GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
				if (null != generatedValue && (generatedValue.strategy() == GenerationType.AUTO 
						|| generatedValue.strategy() == GenerationType.IDENTITY)) {
					autoIncrement = true;
				}
				
				Id id = field.getAnnotation(Id.class);
				Column column = field.getAnnotation(Column.class);
				Basic basic = field.getAnnotation(Basic.class);
				if (null != column) {
					columnNames[i] = column.name().isEmpty() ? field.getName() : column.name();
					columnTypes[i] = loader.getSqlType(field.getType());
				} else if (null == basic || null == id) {
					columnNames[i] = field.getName();
					columnTypes[i] = loader.getSqlType(field.getType());
				}
				
				if(id != null){
					primaryKeyNames.add(columnNames[i]);
				}
				if(!fieldsMap.containsKey(columnNames[i]))
					fieldsMap.put(columnNames[i], field);
			}
			String[] primaryKeys = new String[primaryKeyNames.size()];
			primaryKeyNames.toArray(primaryKeys);
			
			cache.put(clazz.getName(), new DalDefaultJpaParser<T>(
					clazz, loader, autoIncrement, databaseName, tableName, 
					columnNames, primaryKeys, columnTypes, fieldsMap));
		}
		return (DalParser<T>) cache.get(clazz.getName());
	}

	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance = this.clazz.newInstance();
			for (int i = 0; i < this.getColumnNames().length; i++) {
				Field field = this.fieldsMap.get(this.getColumnNames()[i]);
				this.loader.setValue(field, instance,
						rs.getObject(this.getColumnNames()[i]));
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
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (this.getPrimaryKeyNames() != null) {
			for (int i = 0; i < this.getPrimaryKeyNames().length; i++) {
				try {
					Field field = this.fieldsMap.get(this.getPrimaryKeyNames()[i]);
					Object val = this.loader.getValue(field, pojo);
					map.put(this.getPrimaryKeyNames()[i],val);
				} catch (ReflectiveOperationException e) {
					e.printStackTrace();
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
				Object val = this.loader.getValue(field, pojo);
				map.put(this.getColumnNames()[i], val);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

}
