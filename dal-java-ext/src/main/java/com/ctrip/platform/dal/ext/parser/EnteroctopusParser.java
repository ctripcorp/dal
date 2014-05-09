package com.ctrip.platform.dal.ext.parser;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ctrip.fx.enteroctopus.common.jpa.DBColumn;
import com.ctrip.fx.enteroctopus.common.jpa.DBEntity;
import com.ctrip.fx.enteroctopus.common.jpa.DBId;
import com.ctrip.platform.dal.dao.DalParser;

@Deprecated
public class EnteroctopusParser<T> implements DalParser<T>{
	
	private static ConcurrentHashMap<String, DalParser<?>> cache = null;
	
	static{
		cache = new ConcurrentHashMap<String, DalParser<?>>();	
	}
	
	private Loader loader;
	private String databaseName;
	private String tableName;
	private String[] fieldNames;
	private String[] columns;
	private int[] columnTypes;
	private String[] primaryKeys; //The default primary key has only one
	private DBEntity entiryAntot;
	private Class<T> clazz;
	private Field identity;
	private Field[] originFileds;
	
	private EnteroctopusParser(Class<T> clazz, Loader loader){
		this.clazz = clazz;
		this.loader = loader;
		this.entiryAntot = this.clazz.getAnnotation(DBEntity.class);
		this.databaseName = this.entiryAntot.db().name();
		this.tableName = this.entiryAntot.tableName();
		
		Field[] fields = this.clazz.getDeclaredFields();
		
		this.fieldNames = new String[fields.length];
		this.columns = new String[fields.length];
		this.columnTypes = new int[fields.length];
		this.originFileds = new Field[fields.length];
		
		List<String> ids = new ArrayList<String>();
		
		for(int i = 0 ; i< fields.length; i++){
			Field field = fields[i];
			field.setAccessible(true);
			this.originFileds[i] = field;
			DBColumn dbColumn = field.getAnnotation(DBColumn.class);
			this.columns[i] = dbColumn.columnName().equals("") ? 
					field.getName() : dbColumn.columnName();
			this.columnTypes[i] = dbColumn.wrapperType().equals(Void.class) ?
					this.loader.getSqlType(field.getType()) : 
						this.loader.getSqlType(dbColumn.wrapperType());
			this.fieldNames[i] = field.getName();		
			DBId id = field.getAnnotation(DBId.class);
			if(null != id){
				identity = field;
				ids.add(this.columns[i]);				
			}		
		}
		
		this.primaryKeys = new String[ids.size()];
		ids.toArray(this.primaryKeys);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> DalParser<T> create(Class<T> clazz,  Loader loader){
		if(!cache.contains(clazz.getName())){
			cache.put(clazz.getName(), new EnteroctopusParser<T>(clazz, loader));
		}
		return (DalParser<T>)cache.get(clazz.getName());
	}
	
	@Override
	public T map(ResultSet rs, int rowNum) throws SQLException {
		try {
			T instance  = this.clazz.newInstance();
			//Fill fields here
			for(int i = 0; i < fieldNames.length; i++){
				Field field = this.originFileds[i];
				this.loader.setValue(field, instance, rs.getObject(this.columns[i]));
			}
			return instance;
		} catch (Exception e) {
			throw new SQLException(e);
		}
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
		return false;
	}

	@Override
	public Number getIdentityValue(T pojo) {
		if(pojo.getClass().equals(this.clazz)){
			try {
				Object val = this.identity.get(pojo);
				if(val instanceof Number)
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
		Object id = this.getIdentityValue(pojo);
		if(id != null)
			map.put(primaryKeys[0], id);
		return map;
	}

	@Override
	public Map<String, ?> getFields(T pojo) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for(int i = 0; i < this.originFileds.length; i++){
			try {
				Object val = this.loader.getValue(originFileds[i], pojo);
				map.put(this.columns[i], val);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
		return map;
	}
}
