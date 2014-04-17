package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The T type must contain non-parameters constructor
 * @author wcyuan
 * @param <T> The JPA Entity
 */
public class DalDefaultJpaParser<T> implements DalParser<T>{
	
	private static ConcurrentHashMap<String, DalParser<?>> cache = null;
	
	static{
		cache = new ConcurrentHashMap<String, DalParser<?>>();
	}
	
	private String databaseName;
	private String tableName;
	private String[] fieldNames;
	private String[] columns;
	private int[] columnTypes;
	private String[] primaryKeys;
	private Class<T> clazz;
	
	private DalDefaultJpaParser(Class<T> clazz){
		this.clazz = clazz;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> DalParser<T> create(Class<T> clazz){
		if(!cache.contains(clazz.getName())){
			cache.put(clazz.getName(), new DalDefaultJpaParser<T>(clazz));
		}
		return (DalParser<T>)cache.get(clazz.getName());
	}
	
	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance  = this.clazz.getConstructor(new Class<?>[]{}).newInstance(new Object[]{});
			//TODO Fill fields here
			for(int i = 0; i < fieldNames.length; i++){
				Field field = this.clazz.getField(fieldNames[i]);
				if(field.getDeclaringClass().equals(Integer.class)) //Integer
					field.set(instance, rs.getInt(columns[i]));
				if(field.getDeclaringClass().equals(String.class))
					field.set(instance, rs.getString(columns[i])); //String
			}
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getDatabaseName() {
		return this.databaseName;
	}

	@Override
	public String getTableName() {
		return this.tableName;
	}

	@Override
	public String[] getColumnNames() {
		return this.columns;
	}

	@Override
	public String[] getPrimaryKeyNames() {
		return this.primaryKeys;
	}

	@Override
	public int[] getColumnTypes() {
		return this.columnTypes;
	}

	@Override
	public boolean isAutoIncrement() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Number getIdentityValue(T pojo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ?> getPrimaryKeys(T pojo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ?> getFields(T pojo) {
		// TODO Auto-generated method stub
		return null;
	}
}
